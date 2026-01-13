package mau.donate.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        CaffeineCache db1 = new CaffeineCache("DBObject", Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(10_000).build());
        CaffeineCache db2 = new CaffeineCache("DBRow", Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(5_000).build());
        CaffeineCache db3 = new CaffeineCache("DBStats", Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(500).build());
        CaffeineCache db4 = new CaffeineCache("IMG", Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build());
        CaffeineCache db5 = new CaffeineCache("API", Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build());
        cacheManager.setCaches(List.of(db1, db2, db3, db4, db5));
        return cacheManager;
    }
}