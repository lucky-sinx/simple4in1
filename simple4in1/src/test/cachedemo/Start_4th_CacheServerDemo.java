package test.cachedemo;

import simple4in1.CacheServer;

public class Start_4th_CacheServerDemo {
    public static void main(String[] args) {
        CacheServer cacheServer4 = new CacheServer("localhost", 3003);
        cacheServer4.startCacheServer();
    }
}
