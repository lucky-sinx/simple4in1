package simple4in1;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class RMIService {
    public static <I extends Remote> void createRegistry(int port, String name, I object) {
        try {
            LocateRegistry.getRegistry(port).list();
        } catch (Throwable th) {
            try {
                // ???
                UnicastRemoteObject.exportObject(object, 0); // 有程序停留作用，main进程结束后该进程仍继续执行
                Registry registry = LocateRegistry.createRegistry(port);
                registry.rebind(name, object);
            } catch (RemoteException e) {
                LogUtil.severe("[RegistryUtil] [getRegistry] " + e.getClass() + ": " + e.getMessage());
            }
        }
    }

    /**
     * 获取已注册本地进程的引用
     */
    public static Remote getRegistry(String host, int port, String name) {
        try {
//            return Naming.lookup("//localhost:1888/Park");
            Registry registry = LocateRegistry.getRegistry(host, port);
            return registry.lookup(name);
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
            LogUtil.severe("[RegistryUtil] [getRegistry] " + e.getClass() + ": " + e.getMessage());
        }
        return null;
    }

    public static void startPark() {
        String name = ConfigUtil.getParkName();
        String host = ConfigUtil.getParkHost();
//        host = "192.168.10.100";
        int port = ConfigUtil.getParkPort();
        Park park = new Park(host, port, name);
        createRegistry(port, name, park);
        TimerUtil.startParkTimerTask(park);
    }

    public static void startWorker(Worker worker) {
        try {
            DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
            WorkerLocal myworker = (WorkerLocal) dynamicProxy.bind(WorkerLocal.class);
            createRegistry(worker.getPort(), worker.getName(), myworker);

            ParkLocal parkRemote = getPark();
            parkRemote.createWorker(worker.getHost(), worker.getPort(), worker.getName());
            LogUtil.info(String.format("[Worker]%s-%s-%s connect park success", worker.getHost(),
                    worker.getPort(), worker.getName()));
            TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            LogUtil.info(String.format("[Worker]%s-%s-%s start", worker.getHost(),
                    worker.getPort(), worker.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.severe(String.format("[Worker]%s-%s-%s start fail", worker.getHost(),
                    worker.getPort(), worker.getName()));
        }
    }
//        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
//        WorkerLocal myworker = (WorkerLocal) dynamicProxy.bind(WorkerLocal.class);
//        createRegistry(worker.getPort(), worker.getName(), myworker);
//        //获取ParkService
//        try {
//            ParkLocal parkRemote = getPark();
//            if (parkRemote != null) {
//                try {
//                    parkRemote.createWorker(worker.getHost(), worker.getPort(), worker.getName());
//                    LogUtil.info("");
//                } catch (RemoteException e) {
//                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
//                }
//                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
//            }
////            System.out.println(parkRemote.get(worker.getName()));
//        } catch (Exception e) {
//            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
//        }


//    private static void startFileSystem() throws RemoteException {
//
//    }

    public static void startFileServer(FileServer fileServer) {
        //获取ParkService
        try {
            createRegistry(ConfigUtil.getFileSystemLBPort(), "FileServer", fileServer);

            ParkLocal parkLocal = getPark();
            LogUtil.info(String.format("[FileServer]%s connect park success", fileServer.getHost()));
            parkLocal.createFileServer(fileServer.getHost());
            TimerUtil.startFileServerTimerTask(parkLocal, fileServer.getHost());
            LogUtil.info(String.format("[FileServer]%s start", fileServer.getHost()));
        } catch (Exception e) {
            LogUtil.severe(String.format("[FileServer]%s start fail", fileServer.getHost()));
        }
    }

    public static void startCacheServer(CacheServer cacheServer) {
        //获取ParkService
        try {
            createRegistry(cacheServer.getPort(), cacheServer.id, cacheServer);

            ParkLocal parkLocal = getPark();
            LogUtil.info(String.format("[CacheServer]%s connect park success", cacheServer.getId()));
            parkLocal.createCacheServer(cacheServer.getId());

//            TimerUtil.startFileServerTimerTask(parkLocal, cacheServer.getHost());
            TimerUtil.startCacheServerTimerTask(parkLocal, cacheServer.getId());
            LogUtil.info(String.format("[CacheServer]%s start", cacheServer.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.severe(String.format("[CacheServer]%s start fail", cacheServer.getId()));
        }
    }


//        createRegistry(Config.getFileSystemLBPort(), "FileServer", fileServer);
//        //获取ParkService
//        try {
//            ParkLocal parkLocal = getPark();
//            parkLocal.createFileServer(fileServer.getHost());
//            ParkLocal parkRemote = getPark();
//            if (parkRemote != null) {
//                try {
//                    parkLocal.createFileServer(fileServer.getHost());
//                } catch (RemoteException e) {
//                    LogUtil.severe("[Context] [startFileServer] " + e.getClass() + ": " + e.getMessage());
//                }
//                TimerUtil.startFileServerTimerTask(parkLocal, fileServer.getHost());
//            }
//            System.out.println(parkLocal.get(fileServer.getHost()));
//        } catch (RemoteException e) {
//            LogUtil.severe("[Context] [startFileServer] " + e.getClass() + ": " + e.getMessage());
//        }
//    }

    public static LocalFileSystem getFileServer(String host) {
        return (LocalFileSystem) getRegistry(host, ConfigUtil.getFileSystemLBPort(), "FileServer");
    }

    public static ParkLocal getPark() {
        return (ParkLocal) getRegistry(ConfigUtil.getParkHost(), ConfigUtil.getParkPort(), ConfigUtil.getParkName());
    }

    public static WorkerLocal getWorker(String host, int port, String workerName) {
        return (WorkerLocal) getRegistry(host, port, workerName);
    }

    public static CacheServerLocal getCacheServer(String cacheServerId) {
        String[] info = cacheServerId.split(":");
        return getCacheServer(info[1], Integer.parseInt(info[2]), cacheServerId);
    }

    public static CacheServerLocal getCacheServer(String host, int port, String cacheName) {
        return (CacheServerLocal) getRegistry(host, port, cacheName);
    }
}
