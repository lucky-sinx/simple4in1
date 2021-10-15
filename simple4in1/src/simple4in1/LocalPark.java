package simple4in1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * 暴露给Worker和Contractor调用的接口
 */
public interface LocalPark extends Remote{
    void create(String host, int port, String name) throws RemoteException;

    void heartbeat(String host, int port, String name) throws RemoteException;

    Map<String, Map<String, Object>> get(String name) throws RemoteException;
}
