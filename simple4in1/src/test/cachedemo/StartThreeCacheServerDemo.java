package test.cachedemo;

import simple4in1.CacheServer;

public class StartThreeCacheServerDemo {
    public static void main(String[] args) {
        CacheServer cacheServer1 = new CacheServer("localhost", 3000);
        cacheServer1.startCacheServer();
        CacheServer cacheServer2 = new CacheServer("localhost", 3001);
        cacheServer2.startCacheServer();
        CacheServer cacheServer3 = new CacheServer("localhost", 3002);
        cacheServer3.startCacheServer();
    }
}
