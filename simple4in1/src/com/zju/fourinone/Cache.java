package com.zju.fourinone;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * 理解:
 * = Cache*
 */
public class Cache extends Service implements LocalCache {
    private final Map<String, String> caches = new HashMap<>();

    public Cache(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    @Override
    public String getCache(String key) {
        if (!caches.containsKey(key)) {
            LogUtil.warning(String.format("[Cache] [getCache] cache(%s:%s:%s) does not have key(%s)",
                    getHost(), getPort(), getName(), key));
            return null;
        }
        return caches.get(key);
    }

    @Override
    public void putCache(String key, String value) {
        caches.put(key, value);
    }

    @Override
    public void deleteCache(String key) throws RemoteException {
        caches.remove(key);
    }
}
