package simple4in1;

import java.util.concurrent.TimeUnit;

public class CacheServer extends Cache implements CacheServerLocal {
    String id;

    public static void main(String[] args) {
        CacheServer cacheServer1 = new CacheServer("localhost", 3000);
        cacheServer1.startCacheServer();
        CacheServer cacheServer2 = new CacheServer("localhost", 3001);
        cacheServer2.startCacheServer();
        CacheServer cacheServer3 = new CacheServer("localhost", 3002);
        cacheServer3.startCacheServer();
    }

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

    public Object get(String key) {
        if (concurrentHashMap.isEmpty() || !(concurrentHashMap.containsKey(key)))
            return null;
        CacheBase cacheBase = concurrentHashMap.get(key);
        cacheBase.setLastTime(System.currentTimeMillis());
        cacheBase.setCount(cacheBase.getCount() + 1);
        return cacheBase.getValue();
    }

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CacheServer(String host, int port) {
        setHost(host);
        setPort(port);
        setId("CacheServer:" + host + ":" + port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    deleteCacheThread();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteCacheThread() {
        for (String s : concurrentHashMap.keySet()) {
            CacheBase cacheBase = concurrentHashMap.get(s);
            long time = System.currentTimeMillis() - cacheBase.getLastTime();
            if (time > 1000000) {
                concurrentHashMap.remove(s);
                System.out.println("delete" + s);
            }
        }
    }

    public void startCacheServer() {
        RMIService.startCacheServer(this);
    }
}
