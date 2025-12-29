package mau.donate.service;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Cacheable(value = "dbstats")
    public DatabaseObject.DatabaseStats getDatabaseStats() {
        return DatabaseObject.getDatabaseStats();
    }
}
