package simple4in1;

import java.util.concurrent.ConcurrentHashMap;

public class Cache extends Service{
    public ConcurrentHashMap<String, CacheBase> concurrentHashMap = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        if (concurrentHashMap.contains(key)) {
            CacheBase cache = concurrentHashMap.get(key);
            cache.setValue(value);
            cache.setCount(cache.getCount() + 1);
//            cache.setWriteTime(System.currentTimeMillis());
            cache.setLastTime(System.currentTimeMillis());
            return;
        }
        CacheBase newCache = new CacheBase();
        newCache.setValue(value);
        newCache.setCount(1);
        newCache.setWriteTime(System.currentTimeMillis());
        newCache.setLastTime(System.currentTimeMillis());
        concurrentHashMap.put(key, newCache);
    }

    public Object get(System key) {
        if (concurrentHashMap.isEmpty() || !(concurrentHashMap.contains(key)))
            return null;
        CacheBase cacheBase = concurrentHashMap.get(key);
        cacheBase.setLastTime(System.currentTimeMillis());
        cacheBase.setCount(cacheBase.getCount() + 1);
        return cacheBase.getValue();
    }
}
