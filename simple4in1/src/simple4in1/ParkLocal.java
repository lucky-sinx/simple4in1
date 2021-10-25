package simple4in1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * 暴露给Worker和Contractor调用的接口
 */
public interface ParkLocal extends Remote {
    void createWorker(String host, int port, String name) throws RemoteException;

    void createFileServer(String host) throws RemoteException;

    void createCacheServer(String id) throws RemoteException;

    void heartbeatworker(String host, int port, String name) throws RemoteException;

    void heartbeatfileserver(String host) throws RemoteException;

    void heartbeatcacheserver(String host) throws RemoteException;

    boolean putCache(String key, Object value) throws RemoteException;

    Object getCache(String key) throws RemoteException;

    Map<String, Map<String, Object>> get(String name) throws RemoteException;
}
