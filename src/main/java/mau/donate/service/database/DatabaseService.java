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



    @Cacheable(value = "DBObject", key = "T(java.util.Objects).hash(#clazz, #sql, #args != null ? T(java.util.Arrays).deepHashCode(#args) : null)", unless = "#result == null")
    public <T> Optional<T> executeQuery(Class<T> clazz, String sql, Object... args) {
        try {
            SQLCleaner C = new SQLCleaner(sql, args);
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
    public Optional<DatabaseObject.Row> executeQuery(String sql, Object... args) {
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
        return jdbcTemplate.queryForList(C.newSQL, C.newParams).stream().map(DatabaseObject.Row::new).collect(Collectors.toList());
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



    public <T> void refreshID(DatabaseObject<?> dbobject) {
        Cache cache = cacheManager.getCache("DBObject");
        if (cache == null) return;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
        nativeCache.asMap().forEach((key, value) -> {
            if (value instanceof DatabaseObject<?> V && V.getClass() == dbobject.getClass() && V.getID() == dbobject.getID()) {
                cache.evict(key);
            } else if (value instanceof List<?> V2) {
                if (!V2.isEmpty() && V2.getFirst() instanceof DatabaseObject<?> V3 && V3.getClass() == dbobject.getClass()) {
                    if (V2.stream().anyMatch( dbo -> ((DatabaseObject<?>)dbo).getID() == dbobject.getID())) {
                        cache.evict(key);
                    }
                }
            }
        });
    }
}
