package simple4in1;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Cache可被远程调用的接口
 * 理解:
 * = CacheLocal
 */
public interface LocalCache extends Remote {
    String getCache(String key) throws RemoteException;

    void putCache(String key, String value) throws RemoteException;

    void deleteCache(String key) throws RemoteException;
}
