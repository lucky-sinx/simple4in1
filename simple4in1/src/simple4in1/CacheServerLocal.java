package simple4in1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CacheServerLocal extends Remote {
    public void put(String key, Object value) throws RemoteException;

    public Object get(String key) throws RemoteException;
}
