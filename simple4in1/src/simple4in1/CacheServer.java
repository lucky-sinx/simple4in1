package simple4in1;

import java.util.concurrent.TimeUnit;

public class CacheServer extends Cache implements CacheLocal{
    String id;

    public static void main(String[] args) {
        CacheServer cacheServer = new CacheServer("localhost", 3000);
        cacheServer.put("33", 2);
        cacheServer.startCacheServer();
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
        setId("CacheServer_" + host + ":" + port);
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
            if (time > 100000) {
                concurrentHashMap.remove(s);
                System.out.println("delete" + s);
            }
        }
    }

    public void startCacheServer() {
        RMIService.startCacheServer(this);
    }
}
