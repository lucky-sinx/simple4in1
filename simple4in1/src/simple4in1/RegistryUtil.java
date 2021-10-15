package simple4in1;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 使用RMI服务
 */
public class RegistryUtil {

    /**
     * 注册本地进程
     */
    // <I extends Remote>???
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
                LogUtil.severe("[RegistryUtil] [getRegistry] " + e.getClass()+": " + e.getMessage());
            }
        }
    }

    /**
     * 获取已注册本地进程的引用
     */
    public static Remote getRegistry(String host, int port, String name) {
        try {
//            return Naming.lookup("//localhost:1888/Park");
            Registry registry = LocateRegistry.getRegistry(host,port);
            return registry.lookup(name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe("[RegistryUtil] [getRegistry] " + e.getClass()+": " + e.getMessage());
        }
        return null;
    }
}
