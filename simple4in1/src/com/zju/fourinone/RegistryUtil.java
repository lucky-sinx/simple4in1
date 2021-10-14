package com.zju.fourinone;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 使用RMI注册工具
 */
public class RegistryUtil {

    /**
     * 注册本地进程
     * @param port: 端口号
     * @param name: 进程名
     * @param object: 进程
     * @param <I>: 实现或继承Remote的类或接口
     * @throws RemoteException: RemoteException
     */
    // <I extends Remote>???
    public static <I extends Remote> void createRegistry(int port, String name, I object) throws RemoteException {
        // 暴露当前对象，有程序停留作用，main进程结束后该进程仍继续执行
        UnicastRemoteObject.exportObject(object, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(name, object);
    }

    /**
     * 获取已注册本地进程的引用
     * @param port: 端口号
     * @param name: 进程名
     * @return 进程的引用
     * @throws NotBoundException: NotBoundException
     * @throws RemoteException: RemoteException
     */
    public static Object getRegistry(int port, String name) throws NotBoundException, RemoteException {
        // 等同于return Naming.lookup("//localhost:1888/Park");
        Registry registry = LocateRegistry.getRegistry(port);
        return registry.lookup(name);
    }
}
