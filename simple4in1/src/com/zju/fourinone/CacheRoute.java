package com.zju.fourinone;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * Cache路由映射
 * 理解:
 * = CacheFacade
 */
public class CacheRoute extends Cache {
    private final List<String> caches = Config.getCacheServers();

    private enum CacheOperation {
        PUT("put"),
        GET("get"),
        DELETE("delete");

        private final String name;

        CacheOperation(String name) {
            this.name = name;
        }

    }

    public CacheRoute(String host, int port, String name) {
        super(host, port, name);
    }

    @Override
    public String getCache(String key) {
        String res = (String) invoke(CacheOperation.GET, new Object[]{key});
        if (res == null) {
            return "";
        }
        return res;
    }

    @Override
    public void putCache(String key, String value) {
        invoke(CacheOperation.PUT, new Object[]{key, value});
    }

    @Override
    public void deleteCache(String key) {
        invoke(CacheOperation.DELETE, new Object[]{key});
    }

    /**
     * Cache远程调用
     *
     * @param cacheOperation: 缓存操作
     * @param args:           远程调用方法的参数（包括key和value）
     */
    private Object invoke(CacheOperation cacheOperation, Object[] args) {
        String[] cache = caches.get(args[0].hashCode() % caches.size()).split(":");
        int port = Integer.parseInt(cache[1]);
        String name = cache[2];

        LocalCache cacheRemote;
        try {
            cacheRemote = (LocalCache) RegistryUtil.getRegistry(port, name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe(String.format("[CacheRoute] [invoke] connect FileSystem(%s:%s:%s) fail.\n%s",
                    cache[0], cache[1], cache[2], e.getMessage()));
            return null;
        }

        Class<?>[] params = new Class[args.length];
        Arrays.fill(params, String.class);
        try {
            return cacheRemote.getClass()
                    .getMethod(cacheOperation.name, params)
                    .invoke(cacheRemote, args);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            LogUtil.severe(String.format("[CacheRoute] [invoke] invoke Cache(%s:%s:%s) method(%s) fail\n%s",
                    cache[0], cache[1], cache[2], cacheOperation.name, e.getMessage()));
            return null;
        }
    }
}
