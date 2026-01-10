package mau.donate.service;

import mau.donate.service.database.DatabaseObject;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    @Cacheable(value = "tblstats", key = "#name")
    public DatabaseObject.TableStats getTableStats(String name) {
        return DatabaseObject.getTableStats(name);
    }
    @Cacheable(value = "monthlystats", key = "#year + '-' + #month")
    public Map<String, Object> getMonthlyStats(long year, long month) {
        return DatabaseObject.doQuery("call maudonate.MonthlyStat(?,?);", year, month).orElseThrow().columns;
    }

}
