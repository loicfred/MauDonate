package mau.donate.service.database;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import static mau.donate.service.database.DatabaseObject.*;

@Service
public class DatabaseService {
    protected static JdbcTemplate jdbcTemplate;

    private final CacheManager cacheManager;
    public DatabaseService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public static void setJdbcTemplate(JdbcTemplate template) {
        jdbcTemplate = template;
    }

    protected static <T> T mapResultSetToObject(ResultSet rs, Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            T item = ctor.newInstance();
            Class<?> clz = clazz;
            while (clz != null) {
                for (Field f : clz.getDeclaredFields()) {
                    if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    Object value;
                    try {
                        value = rs.getObject(f.getName());
                    } catch (Exception ignored) {
                        value = null;
                    }
                    if (value != null) {
                        switch (value) {
                            case java.sql.Date D -> f.set(item, D.toLocalDate());
                            case java.sql.Timestamp D -> f.set(item, D.toLocalDateTime());
                            case java.sql.Time D -> f.set(item, D.toLocalTime());
                            case BigDecimal D -> {
                                if (f.getType() == long.class) {
                                    f.set(item, D.longValue());
                                } else if (f.getType() == int.class) {
                                    f.set(item, D.intValue());
                                } else if (f.getType() == double.class) {
                                    f.set(item, D.doubleValue());
                                } else if (f.getType() == short.class) {
                                    f.set(item, D.shortValue());
                                } else if (f.getType() == float.class) {
                                    f.set(item, D.floatValue());
                                } else if (f.getType() == byte.class) {
                                    f.set(item, D.byteValue());
                                }
                            }
                            default -> f.set(item, value);
                        }
                    }
                }
                clz = clz.getSuperclass();
            }
            return item;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map ResultSet to " + clazz.getSimpleName(), e);
        }
    }



    @Cacheable(value = "DBObject", key = "T(java.util.Objects).hash(#clazz, #sql, #args != null ? T(java.util.Arrays).deepHashCode(#args) : null)", unless = "#result == null")
    public <T> Optional<T> executeQuery(Class<T> clazz, String sql, Object... args) {
        try {
            SQLCleaner C = new DatabaseObject.SQLCleaner(sql, args);
            return Optional.ofNullable(jdbcTemplate.queryForObject(C.newSQL, (rs, rowNum) -> mapResultSetToObject(rs, clazz), C.newParams));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
    @Cacheable(value = "DBObject", key = "T(java.util.Objects).hash(#clazz, #sql, T(java.util.Arrays).deepHashCode(#args))", unless = "#result == null")
    public <T> List<T> executeQueryAll(Class<T> clazz, String sql, Object... args) {
        SQLCleaner C = new SQLCleaner(sql, args);
        return jdbcTemplate.query(C.newSQL, (rs, rowNum) -> mapResultSetToObject(rs, clazz), C.newParams);
    }



    @Cacheable(value = "DBRow", key = "T(java.util.Objects).hash(#sql, T(java.util.Arrays).deepHashCode(#args))", unless = "#result == null")
    public Optional<Row> executeQuery(String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
            return Optional.of(new Row(jdbcTemplate.queryForMap(C.newSQL, C.newParams)));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
    @Cacheable(value = "DBRow", key = "T(java.util.Objects).hash(#sql, T(java.util.Arrays).deepHashCode(#args))", unless = "#result == null")
    public List<Row> executeQueryAll(String sql, Object... args) {
        SQLCleaner C = new SQLCleaner(sql, args);
        return jdbcTemplate.queryForList(C.newSQL, C.newParams).stream().map(Row::new).collect(Collectors.toList());
    }
    @Cacheable(value = "DBRow", key = "T(java.util.Objects).hash(#clazz, #sql, T(java.util.Arrays).deepHashCode(#args))", unless = "#result == null")
    public <T> Optional<T> executeQueryValue(Class<T> clazz, String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
            return Optional.ofNullable(jdbcTemplate.queryForObject(C.newSQL, clazz, C.newParams));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Cacheable(value = "DBStats", key = "#name", unless = "#result == null")
    public DatabaseObject.TableStats getTableStats(String name) {
        DatabaseObject.TableStats stats = new DatabaseObject.TableStats();
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

    @Cacheable(value = "DBStats", key = "'DBSTATISTICS'", unless = "#result == null")
    public DatabaseObject.DatabaseStats getDatabaseStats() {
        DatabaseObject.DatabaseStats stats = new DatabaseObject.DatabaseStats();
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

    public void refreshID(DatabaseObject<?> dbobject) {
        Cache cache = cacheManager.getCache("DBObject");
        if (cache == null) return;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
        nativeCache.asMap().forEach((key, value) -> {
            if (value instanceof DatabaseObject<?> V && V.getClass() == dbobject.getClass() && V.hashedIdentifiers().equals(dbobject.hashedIdentifiers())) {
                cache.evict(key);
            } else if (value instanceof List<?> V2) {
                if (!V2.isEmpty() && V2.getFirst() instanceof DatabaseObject<?> V3 && V3.getClass() == dbobject.getClass()) {
                    if (V2.stream().anyMatch( dbo -> ((DatabaseObject<?>)dbo).hashedIdentifiers().equals(dbobject.hashedIdentifiers()))) {
                        cache.evict(key);
                    }
                }
            }
        });
    }
}
