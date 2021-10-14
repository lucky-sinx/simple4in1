

package com.zju.fourinone;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 理解：
 * = BeanContext, ServiceContext, BeanService
 * Park, Worker and Contractor == Spring Bean
 */
public class Context {
    public static void startContext() {
        startPark();
        startWeb();
        startFileSystem();
        startCache();
    }

    private static void startPark() {
        String name = Config.getParkName();
        String host = Config.getParkHost();
        int port = Config.getParkPort();
        Park park = new Park(host, port, name);
        try {
            RegistryUtil.createRegistry(port, name, park);
            TimerUtil.startParkTimerTask(park);
            LogUtil.info("[Fourinone] Park has been started");
        } catch (RemoteException e) {
            LogUtil.severe(String.format("[Context] [startPark] Park(%s:%s:%s) start fail.\n%s",
                    host, port, name, e.getMessage()));
        }
    }

    private static void startWeb() {
        String host = Config.getWebHost();
        int port = Config.getWebPort();
        try {
            HttpUtil.startHttpServer(host, port, 0);
            LogUtil.info("[Fourinone] Web has been started");
        } catch (IOException e) {
            LogUtil.severe(String.format("[Context] [startWeb] Web(%s:%s) start fail.\n%s",
                    host, port, e.getMessage()));
        }
    }

    private static void startFileSystem() {
        String name = Config.getFileSystemLBName();
        String host = Config.getFileSystemLBHost();
        int port = Config.getFileSystemLBPort();
        FileSystemLB fileSystemLB = new FileSystemLB(host, port, name);
        try {
            RegistryUtil.createRegistry(port, name, fileSystemLB);
            LogUtil.info("[FileSystem] FileSystemLB has been started");
        } catch (RemoteException e) {
            LogUtil.severe(String.format("[Context] [startFileSystem] FileSystemLB(%s:%s:%s) start fail.\n%s",
                    host, port, name, e.getMessage()));
        }

        List<String> fileSystems = Config.getFileSystemServers();
        for (String fileSystemInfo : fileSystems) {
            String[] splits = fileSystemInfo.split(":");
            String fileSystemHost = splits[0];
            int fileSystemPort = Integer.parseInt(splits[1]);
            String fileSystemName = splits[2];
            FileSystem fileSystem = new FileSystem(fileSystemHost, fileSystemPort, fileSystemName);
            try {
                RegistryUtil.createRegistry(fileSystemPort, fileSystemName, fileSystem);
                LogUtil.info(String.format("[FileSystem] FileSystem(%s) has been started", fileSystemName));
            } catch (RemoteException e) {
                LogUtil.severe(String.format("[Context] [startFileSystem] FileSystem(%s) start fail.\n%s",
                        fileSystemInfo, e.getMessage()));
            }
        }
        LogUtil.info("[Fourinone] FileSystem has been started.");
    }

    private static void startCache() {
        String name = Config.getCacheRoutName();
        String host = Config.getCacheRoutHost();
        int port = Config.getCacheRoutPort();
        CacheRoute cacheRoute = new CacheRoute(host, port, name);
        try {
            RegistryUtil.createRegistry(port, name, cacheRoute);
            LogUtil.info("[Cache] CacheRoute has been started");
        } catch (RemoteException e) {
            LogUtil.severe(String.format("[Context] [startCache] CacheRoute(%s:%s:%s) start fail.\n%s",
                    host, port, name, e.getMessage()));
        }

        List<String> caches = Config.getCacheServers();
        for (String cacheInfo : caches) {
            String[] splits = cacheInfo.split(":");
            String cacheHost = splits[0];
            int cachePort = Integer.parseInt(splits[1]);
            String cacheName = splits[2];
            Cache cache = new Cache(cacheHost, cachePort, cacheName);
            try {
                RegistryUtil.createRegistry(cachePort, cacheName, cache);
                LogUtil.info(String.format("[Cache] Cache(%s) has been started", cacheName));
            } catch (RemoteException e) {
                LogUtil.severe(String.format("[Context] [startCache] Cache(%s) start fail.\n%s",
                        cacheInfo, e.getMessage()));
                return;
            }
        }
        LogUtil.info("[Fourinone] Cache has been started.");
    }

    static void startWorker(Worker worker) throws RemoteException {
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
        LocalWorker myworker = (LocalWorker) dynamicProxy.bind(LocalWorker.class);
        RegistryUtil.createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {
            LocalPark localPark = getPark();
            localPark.create(worker.getHost(), worker.getPort(), worker.getName());
            LocalPark parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.create(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(localPark.get(worker.getName()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
        }
    }


    public static LocalPark getPark() {
        String host = Config.getParkHost();
        int port = Config.getParkPort();
        String name = Config.getParkName();
        try {
            return (LocalPark) RegistryUtil.getRegistry(port, name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe(String.format("[Context] [getPark] connect Park(%s:%s:%s) fail.\n%s",
                    host, port, name, e.getMessage()));
            return null;
        }
    }

    public static LocalFileSystem getFileSystem() {
        String host = Config.getFileSystemLBHost();
        int port = Config.getFileSystemLBPort();
        String name = Config.getFileSystemLBName();
        try {
            return (LocalFileSystem) RegistryUtil.getRegistry(port, name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe(String.format("[Context] [getFileSystem] connect FileSystem(%s:%s:%s) fail.\n%s",
                    host, port, name, e.getMessage()));
            return null;
        }
    }

    public static LocalCache getCache() {
        String host = Config.getCacheRoutHost();
        int port = Config.getCacheRoutPort();
        String name = Config.getCacheRoutName();
        try {
            return (LocalCache) RegistryUtil.getRegistry(port, name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe(String.format("[Context] [getCache] connect Cache(%s:%s:%s) fail.\n%s",
                    host, port, name, e.getMessage()));
            return null;
        }
    }

    public static LocalWorker getWorker(String host, int port, String workerName) throws NotBoundException, RemoteException {
        return (LocalWorker) RegistryUtil.getRegistry(port, workerName);
    }
}