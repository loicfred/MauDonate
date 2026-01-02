package mau.donate.service.database;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                    Object value = rs.getObject(f.getName());
                    if (value != null) {
                        if (value instanceof java.sql.Date D) {
                            f.set(item, D.toLocalDate());
                        } else if (value instanceof java.sql.Timestamp D) {
                            f.set(item, D.toLocalDateTime());
                        } else if (value instanceof BigDecimal D) {
                            if (f.getType() == float.class) {
                                f.set(item, D.floatValue());
                            } else if (f.getType() == long.class) {
                                f.set(item, D.longValue());
                            } else if (f.getType() == int.class) {
                                f.set(item, D.intValue());
                            } else if (f.getType() == short.class) {
                                f.set(item, D.shortValue());
                            } else if (f.getType() == byte.class) {
                                f.set(item, D.byteValue());
                            } else if (f.getType() == double.class) {
                                f.set(item, D.doubleValue());
                            }
                        } else {
                            f.set(item, value);
                        }
                    }
                }
                clz = clz.getSuperclass();
            }
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map ResultSet to " + clazz.getSimpleName(), e);
        }
    }



    @Cacheable(value = "Database", key = "T(java.util.Objects).hash(#clazz, #sql, #args != null ? T(java.util.Arrays).deepHashCode(#args) : null)")
    public <T> Optional<T> executeQuery(Class<T> clazz, String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
            return Optional.ofNullable(jdbcTemplate.queryForObject(C.newSQL, (rs, rowNum) -> mapResultSetToObject(rs, clazz), C.newParams));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
    @Cacheable(value = "Database", key = "T(java.util.Objects).hash(#sql, T(java.util.Arrays).deepHashCode(#args))")
    public Optional<DatabaseObject.Row> executeQuery(String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
            return Optional.ofNullable(new Row(jdbcTemplate.queryForMap(C.newSQL, C.newParams)));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }



    @Cacheable(value = "Database", key = "T(java.util.Objects).hash(#clazz, #sql, T(java.util.Arrays).deepHashCode(#args))")
    public <T> List<T> executeQueryAll(Class<T> clazz, String sql, Object... args) {
        SQLCleaner C = new SQLCleaner(sql, args);
        return jdbcTemplate.query(C.newSQL, (rs, rowNum) -> mapResultSetToObject(rs, clazz), C.newParams);
    }
    @Cacheable(value = "Database", key = "T(java.util.Objects).hash(#sql, T(java.util.Arrays).deepHashCode(#args))")
    public List<Row> executeQueryAll(String sql, Object... args) {
        SQLCleaner C = new SQLCleaner(sql, args);
        return jdbcTemplate.queryForList(C.newSQL, C.newParams).stream().map(DatabaseObject.Row::new).collect(Collectors.toList());
    }



    @Cacheable(value = "Database", key = "T(java.util.Objects).hash(#clazz, #sql, T(java.util.Arrays).deepHashCode(#args))")
    public <T> Optional<T> executeQueryValue(Class<T> clazz, String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
            return Optional.ofNullable(jdbcTemplate.queryForObject(C.newSQL, clazz, C.newParams));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }



    public <T> void refreshCache(Class<T> clazz, String sql, Object... args) {
        Cache c = cacheManager.getCache("Database");
        if (c != null) {
            if (clazz != null) c.evictIfPresent(Objects.hash(clazz, sql, args != null ? Arrays.deepHashCode(args) : null));
            c.evictIfPresent(Objects.hash(sql, args != null ? Arrays.deepHashCode(args) : null));
        }
    }
    public <T> void refreshCache(String sql, Object... args) {
        refreshCache(null, sql, args);
    }
    public <T> void refreshWhere(Class<T> clazz, String where, Object... args) {
        refreshCache(clazz, "SELECT * FROM " + getTableName(clazz) + " WHERE " + where + " LIMIT 1;", args);
    }
    public <T> void refreshAllWhere(Class<T> clazz, String where, Object... args) {
        refreshCache(clazz, "SELECT * FROM " + getTableName(clazz) + " WHERE " + where, args);
    }
    public <T> void refreshID(Class<T> clazz, Object ID) {
        refreshWhere(clazz, "ID = ?", ID);
        refreshAllWhere(clazz, "ID = ?", ID);
    }
}
