package com.zju.fourinone;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * FileSystem可被远程调用的接口
 * 理解:
 * = FttpLocal
 */
public interface LocalFileSystem extends Remote {
    boolean exists(String filePath) throws RemoteException;

    boolean isFile(String filePath) throws RemoteException;

    boolean isDirectory(String filePath) throws RemoteException;

    boolean isHidden(String filePath) throws RemoteException;

    boolean isAbsolute(String filePath) throws RemoteException;

    boolean canRead(String filePath) throws RemoteException;

    boolean canWrite(String filePath) throws RemoteException;

    boolean canExecute(String filePath) throws RemoteException;

    long lastModified(String filePath) throws RemoteException;

    long length(String filePath) throws RemoteException;

    boolean delete(String filePath) throws RemoteException;

    boolean mkdir(String filePath) throws RemoteException;

    String read(String filePath) throws RemoteException;

    void write(String filePath, String content) throws RemoteException;
}
