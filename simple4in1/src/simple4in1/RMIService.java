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
            LogUtil.severe("[RegistryUtil] [getRegistry] " + e.getClass() + ": " + e.getMessage());
        }
        return null;
    }

    public static void startPark() {
        String name = Config.getParkName();
        String host = Config.getParkHost();
        int port = Config.getParkPort();
        Park park = new Park(host, port, name);
        createRegistry(port, name, park);
        TimerUtil.startParkTimerTask(park);
    }

    public static void startWorker(Worker worker) {
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
        WorkerLocal myworker = (WorkerLocal) dynamicProxy.bind(WorkerLocal.class);
        createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {
            ParkLocal parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.createWorker(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(parkRemote.get(worker.getName()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
        }
    }

//    private static void startFileSystem() throws RemoteException {
//
//    }

    public static void startFileServer(FileServer fileServer) {
        createRegistry(Config.getFileSystemLBPort(), "FileServer", fileServer);
        //获取ParkService
        try {
            ParkLocal parkLocal = getPark();
            parkLocal.createFileServer(fileServer.getHost());
            ParkLocal parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkLocal.createFileServer(fileServer.getHost());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startFileServer] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startFileServerTimerTask(parkLocal,fileServer.getHost());
            }
            System.out.println(parkLocal.get(fileServer.getHost()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startFileServer] " + e.getClass() + ": " + e.getMessage());
        }
    }

    public static LocalFileSystem getFileServer(String host) {
        return (LocalFileSystem) getRegistry(host, Config.getFileSystemLBPort(), "FileServer");
    }

    public static ParkLocal getPark() {
        return (ParkLocal) getRegistry(Config.getParkHost(), Config.getParkPort(), Config.getParkName());
    }

    public static WorkerLocal getWorker(String host, int port, String workerName) {
        return (WorkerLocal) getRegistry(host, port, workerName);
    }
}
