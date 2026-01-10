package mau.donate.service.database;


import mau.donate.service.CacheService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import static mau.donate.config.AppConfig.dbService;
import static mau.donate.service.database.DatabaseService.jdbcTemplate;
import static mau.donate.service.database.DatabaseService.mapResultSetToObject;
import static my.utilities.json.JSONItem.GSON;

@SuppressWarnings("all")
public abstract class DatabaseObject<T> {

    protected transient final Class<T> entityClass;
    protected transient final List<Field> cachedFields;
    protected transient final RowMapper<T> rowMapper;
    protected transient final String tableName;

    public long ID;

    public long getID() {
        return ID;
    }
    public void setID(long id) {
        ID = id;
    }

    protected DatabaseObject() {
        this.entityClass = (Class<T>) getClass();
        this.cachedFields = new ArrayList<>();

        TableName annotation = entityClass.getAnnotation(TableName.class);
        if (annotation != null) tableName = annotation.value().toLowerCase();
        else tableName = entityClass.getSimpleName().toLowerCase();

        Class<?> clz = entityClass;
        while (clz != null) {
            this.cachedFields.addAll(Arrays.stream(clz.getDeclaredFields()).filter(f -> !Modifier.isTransient(f.getModifiers())).peek(f -> f.setAccessible(true)).collect(Collectors.toList()));
            clz = clz.getSuperclass();
        }
        this.rowMapper = (rs, rowNum) -> mapResultSetToObject(rs, entityClass);
    }

    protected List<String> IDFields() {
        return List.of("ID");
    }

    public int Write() {
        try {
            Result result = getResult(false);
            String sql = "INSERT INTO " + tableName + " (" + result.columns() + ") VALUES (" + result.placeholders() + ")";
            return jdbcTemplate.update(sql, result.values());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object", e);
        }
    }
    public Optional<T> WriteThenReturn() {
        try {
            Result result = getResult(false);
            String sql = "INSERT INTO " + tableName + " (" + result.columns() + ") VALUES (" + result.placeholders() + ") RETURNING *";
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapResultSetToObject(rs, entityClass), result.values()).stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object", e);
        }
    }

    public int Upsert() {
        Result result = getResult(true);
        String sql = "INSERT INTO " + tableName + " (" + result.columns() + ") VALUES (" + result.placeholders() + ") ON DUPLICATE KEY UPDATE " + result.updateClause();
        return jdbcTemplate.update(sql, result.values());
    }
    public Optional<T> UpsertThenReturn() {
        try {
            Result result = getResult(true);
            String sql = "INSERT INTO " + tableName + " (" + result.columns() + ") VALUES (" + result.placeholders() + ") ON DUPLICATE KEY UPDATE " + result.updateClause() + " RETURNING *";
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapResultSetToObject(rs, entityClass), result.values()).stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object", e);
        } finally {
            dbService.refreshID(this);
        }
    }

    public int Update() {
        try {
            String setClause = cachedFields.stream().map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
            List<Object> setValues = cachedFields.stream()
                    .map(f -> {
                        try { return f.get(this); }
                        catch (IllegalAccessException e) { throw new RuntimeException(e); }
                    }).collect(Collectors.toList());

            String whereClause = IDFields().stream().map(ID -> ID + " = ?").collect(Collectors.joining(" AND "));
            List<Object> whereValues = new ArrayList<>();
            for (String ID : IDFields()) whereValues.add(cachedFields.stream().filter(f -> f.getName().equalsIgnoreCase(ID)).findFirst().orElseThrow().get(this));

            List<Object> finalValues = new ArrayList<>();
            finalValues.addAll(setValues);
            finalValues.addAll(whereValues);

            String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
            SQLCleaner C = new SQLCleaner(sql, finalValues);
            return jdbcTemplate.update(C.newSQL, C.newParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No ID field found in " + tableName + ".");
        } finally {
            dbService.refreshID(this);
        }
    }
    public int UpdateOnly(String... columns) {
        try {
            String setClause = cachedFields.stream().filter(f -> Arrays.stream(columns).anyMatch(c -> c == f.getName())).map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
            List<Object> setValues = cachedFields.stream().filter(f -> Arrays.stream(columns).anyMatch(c -> c == f.getName()))
                    .map(f -> {
                        try { return f.get(this); }
                        catch (IllegalAccessException e) { throw new RuntimeException(e); }
                    }).collect(Collectors.toList());

            String whereClause = IDFields().stream().map(ID -> ID + " = ?").collect(Collectors.joining(" AND "));
            List<Object> whereValues = new ArrayList<>();
            for (String ID : IDFields()) whereValues.add(cachedFields.stream().filter(f -> f.getName().equalsIgnoreCase(ID)).findFirst().orElseThrow().get(this));

            List<Object> finalValues = new ArrayList<>();
            finalValues.addAll(setValues);
            finalValues.addAll(whereValues);

            String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
            SQLCleaner C = new SQLCleaner(sql, finalValues);
            return jdbcTemplate.update(C.newSQL, C.newParams);
        } catch (Exception e) {
            throw new RuntimeException("No ID field found in " + tableName + ".");
        } finally {
            dbService.refreshID(this);
        }
    }
    public int Delete() {
        try {
            List<Object> values = new ArrayList<>();
            for (String ID : IDFields()) values.add(cachedFields.stream().filter(f -> f.getName().equalsIgnoreCase(ID)).findFirst().orElseThrow().get(this));
            String sql = "DELETE FROM " + tableName + " WHERE " + IDFields().stream().map(ID -> ID + " = ?").collect(Collectors.joining(" AND "));

            SQLCleaner C = new SQLCleaner(sql, values);
            return jdbcTemplate.update(C.newSQL, C.newParams);
        } catch (Exception e) {
            throw new RuntimeException("No ID field found in " + tableName + ".");
        } finally {
            dbService.refreshID(this);
        }
    }
    public static <T> int Count(Class<T> clazz) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + getTableName(clazz), Integer.class);
    }
    public static <T> int Count(Class<T> clazz, String whereClause, Object... args) {
        SQLCleaner C = new SQLCleaner(whereClause, args);
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + getTableName(clazz) + " WHERE " + C.newSQL, Integer.class, C.newParams);
    }
    public static <T> T getRandom(Class<T> clazz) {
        try {
            List<T> Items = getAll(clazz);
            return Items.get(new Random().nextInt(Items.size()));
        } catch (Exception ignored) {
            return null;
        }
    }
    public static <T> T getRandom(Class<T> clazz, String whereClause, Object... args) {
        try {
            List<T> Items = getAllWhere(clazz, whereClause, args);
            return Items.get(new Random().nextInt(Items.size()));
        } catch (Exception ignored) {
            return null;
        }
    }

    public int IncrementColumn(String column, int amount) {
        try {
            String setClause = column + " = " + column + " + ?";
            List<Object> setValues = List.of(amount);

            String whereClause = IDFields().stream().map(ID -> ID + " = ?").collect(Collectors.joining(" AND "));
            List<Object> whereValues = new ArrayList<>();
            for (String ID : IDFields()) whereValues.add(cachedFields.stream().filter(f -> f.getName().equalsIgnoreCase(ID)).findFirst().orElseThrow().get(this));

            List<Object> finalValues = new ArrayList<>();
            finalValues.addAll(setValues);
            finalValues.addAll(whereValues);

            String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
            SQLCleaner C = new SQLCleaner(sql, finalValues);
            return jdbcTemplate.update(C.newSQL, C.newParams);
        } catch (Exception e) {
            throw new RuntimeException("No ID field found in " + tableName + ".");
        } finally {
            dbService.refreshID(this);
        }
    }
    public int IncrementColumns(Map<String, Object> parameters) {
        try {
            String setClause = parameters.entrySet().stream().map(f -> f.getKey() + " = " + f.getKey() + " + ?").collect(Collectors.joining(", "));
            List<Object> setValues = parameters.entrySet().stream().map(f -> f.getValue()).collect(Collectors.toList());

            String whereClause = IDFields().stream().map(ID -> ID + " = ?").collect(Collectors.joining(" AND "));
            List<Object> whereValues = new ArrayList<>();
            for (String ID : IDFields()) whereValues.add(cachedFields.stream().filter(f -> f.getName().equalsIgnoreCase(ID)).findFirst().orElseThrow().get(this));

            List<Object> finalValues = new ArrayList<>();
            finalValues.addAll(setValues);
            finalValues.addAll(whereValues);

            String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
            SQLCleaner C = new SQLCleaner(sql, finalValues);
            return jdbcTemplate.update(C.newSQL, C.newParams);
        } catch (Exception e) {
            throw new RuntimeException("No ID field found in " + tableName + ".");
        } finally {
            dbService.refreshID(this);
        }
    }

    private record Result(String columns, String placeholders, Object[] values, String updateClause) {}
    private Result getResult(boolean update) {
        List<Field> nonNullFields = cachedFields.stream().filter(f -> {
            try {
                return f.get(this) != null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        String columns = nonNullFields.stream().map(Field::getName).collect(Collectors.joining(", "));
        String placeholders = nonNullFields.stream().map(p -> "?").collect(Collectors.joining(", "));

        List<Object> values = nonNullFields.stream().map(f -> {
            try { return f.get(this); }
            catch (IllegalAccessException e) { throw new RuntimeException(e); }
        }).toList();

        if (!update) new Result(columns, placeholders, values.toArray(), null);
        String updateClause = cachedFields.stream()
                .map(f -> f.getName() + " = VALUES(" + f.getName() + ")")
                .collect(Collectors.joining(", "));
        return new Result(columns, placeholders, values.toArray(), updateClause);
    }

    public static <T> Optional<T> getById(Class<T> clazz, Object id) {
        return doQuery(clazz, "SELECT * FROM " + getTableName(clazz) + " WHERE ID = ? LIMIT 1;", id);
    }
    public static <T> Optional<T> getWhere(Class<T> clazz, String whereClause, Object... args) {
        return doQuery(clazz, "SELECT * FROM " + getTableName(clazz) + " WHERE " + whereClause + " LIMIT 1;", args);
    }
    public static <T> Optional<T> doQuery(Class<T> clazz, String sql, Object... args) {
        return dbService.executeQuery(clazz, sql, args);
    }

    public static <T> List<T> getAll(Class<T> clazz) {
        return doQueryAll(clazz, "SELECT * FROM " + getTableName(clazz), null);
    }
    public static <T> List<T> getAllWhere(Class<T> clazz, String whereClause, Object... args) {
        return doQueryAll(clazz, "SELECT * FROM " + getTableName(clazz) + " WHERE " + whereClause, args);
    }
    public static <T> List<T> doQueryAll(Class<T> clazz, String sql, Object... args) {
        return dbService.executeQueryAll(clazz, sql, args);
    }

    public static Optional<DatabaseObject.Row> doQuery(String sql, Object... args) {
        return dbService.executeQuery(sql, args);
    }
    public static List<Row> doQueryAll(String sql, Object... args) {
        return dbService.executeQueryAll(sql, args);
    }

    public static <T> Optional<T> doQueryValue(Class<T> clazz, String sql, Object... args) {
        return dbService.executeQueryValue(clazz, sql, args);
    }


    public static int doUpdate(String sql, Object... args) {
        SQLCleaner C = new SQLCleaner(sql, args);
        return jdbcTemplate.update(C.newSQL, C.newParams);
    }


    public String toJSON() {
        return GSON.toJson(this);
    }

    protected static String getTableName(Class<?> clazz) {
        TableName annotation = clazz.getAnnotation(TableName.class);
        if (annotation != null) return annotation.value().toLowerCase();
        return clazz.getSimpleName().toLowerCase();
    }


    public static class Row {
        public transient Map<String, Object> columns;

        public Row(Map<String, Object> qp) {
            this.columns = qp;
        }

        public <T> T get(Class<T> clazz, String fieldName) {
            return clazz.cast(get(fieldName));
        }

        public Object get(String fieldName) {
            return columns.get(fieldName);
        }

        public String getAsString(String fieldName) {
            try {
                return get(fieldName).toString();
            } catch (Exception ignored) {
                return null;
            }
        }

        public int getAsInt(String fieldName) {
            try {
                return Integer.parseInt(getAsString(fieldName));
            } catch (Exception ignored) {
                return 0;
            }
        }

        public long getAsLong(String fieldName) {
            try {
                return Long.parseLong(getAsString(fieldName));
            } catch (Exception ignored) {
                return 0;
            }
        }

        public double getAsDouble(String fieldName) {
            try {
                return Double.parseDouble(getAsString(fieldName));
            } catch (Exception ignored) {
                return 0;
            }
        }

        public short getAsShort(String fieldName) {
            try {
                return get(fieldName) == null ? 0 : Short.parseShort(getAsString(fieldName));
            } catch (Exception ignored) {
                return 0;
            }
        }

        public float getAsFloat(String fieldName) {
            try {
                return Float.parseFloat(getAsString(fieldName));
            } catch (Exception ignored) {
                return 0;
            }
        }

        public boolean getAsBoolean(String fieldName) {
            try {
                return Boolean.parseBoolean(getAsString(fieldName));
            } catch (Exception ignored) {
                return false;
            }
        }

        public byte getAsByte(String fieldName) {
            return Byte.parseByte(getAsString(fieldName));
        }
    }

    public static class SQLCleaner {
        public String newSQL;
        public Object[] newParams;

        public SQLCleaner(String sql, List<Object> params) {
            fixNullParams(sql, params);
        }
        public SQLCleaner(String sql, Object[] params) {
           if (params == null) {
               newSQL = sql;
               return;
           }
            fixNullParams(sql, Arrays.asList(params));
        }

        public void fixNullParams(String sql, List<Object> params) {
            sql = sql.replaceAll("\\s*=\\s*\\?", "=?");

            StringBuilder newSql = new StringBuilder();
            List<Object> newParams = new ArrayList<>();

            int paramIndex = 0;
            int pos = 0;

            int wherePos = -1;
            {
                String upperSql = sql.toUpperCase();
                wherePos = upperSql.indexOf("WHERE ");
                if (wherePos == -1)
                    wherePos = upperSql.indexOf("WHERE\n");
                if (wherePos == -1)
                    wherePos = upperSql.indexOf("WHERE\t");
                if (wherePos == -1)
                    wherePos = upperSql.indexOf("WHERE"); // fallback
            }

            while (pos < sql.length()) {
                int qIndex = sql.indexOf("?", pos);
                if (qIndex == -1 || paramIndex >= params.size()) {
                    newSql.append(sql.substring(pos));
                    break;
                }

                newSql.append(sql, pos, qIndex);
                Object value = params.get(paramIndex);
                boolean isEqualsParam = false;

                // Only check for "=" before the "?" and if we are after WHERE
                int check = qIndex - 1;
                while (check >= 0 && Character.isWhitespace(sql.charAt(check))) check--;
                if (check >= 0 && sql.charAt(check) == '=') {
                    isEqualsParam = (qIndex > wherePos && wherePos != -1);
                }

                if (isEqualsParam && value == null) {
                    // Replace "= ?" with "IS NULL"
                    int lastEq = newSql.lastIndexOf("=");
                    if (lastEq != -1) newSql.deleteCharAt(lastEq);
                    newSql.append(" IS NULL");
                } else {
                    newSql.append("?");
                    newParams.add(value);
                }

                pos = qIndex + 1;
                paramIndex++;
            }

            this.newSQL = newSql.toString();
            this.newParams = newParams.toArray();
        }
    }


    public static DatabaseStats getDatabaseStats() {
        DatabaseStats stats = new DatabaseStats();
        jdbcTemplate.execute((Connection con) -> {
            DatabaseMetaData metaData = con.getMetaData();
            try (ResultSet tables = metaData.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    stats.totalTables++;
                    stats.tableNames.add(tableName);
                }
            }

            // --- Views ---
            try (ResultSet views = metaData.getTables(con.getCatalog(), null, "%", new String[]{"VIEW"})) {
                while (views.next()) {
                    String viewName = views.getString("TABLE_NAME");
                    stats.totalViews++;
                    stats.viewNames.add(viewName);
                }
            }

            stats.totalRows = jdbcTemplate.queryForObject("""
            SELECT SUM(TABLE_ROWS) AS total_rows
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
            AND TABLE_TYPE = 'BASE TABLE';
            """, Long.class).intValue();

            return null;
        });
        return stats;
    }
    public static TableStats getTableStats(String name) {
        TableStats stats = new TableStats();
        jdbcTemplate.execute((Connection con) -> {
            stats.tableName = name.toLowerCase();
            DatabaseMetaData metaData = con.getMetaData();
            try (ResultSet columns = metaData.getColumns(con.getCatalog(), null, stats.tableName, null)) {
                while (columns.next()) {
                    stats.columnNames.add(columns.getString("COLUMN_NAME"));
                }
            }
            try (ResultSet count = con.createStatement().executeQuery("SELECT COUNT(*) FROM " + stats.tableName)) {
                if (count.next()) {
                    stats.totalRows = count.getLong(1);
                }
            }
            return null;
        });
        return stats;
    }
    public static class DatabaseStats {
        public int totalTables = 0;
        public int totalViews = 0;
        public long totalRows = 0;
        public List<String> tableNames = new ArrayList<>();
        public List<String> viewNames = new ArrayList<>();
    }
    public static class TableStats {
        public String tableName;
        public long totalRows = 0;
        public List<String> columnNames = new ArrayList<>();
    }

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TableName {
        String value();
    }


}