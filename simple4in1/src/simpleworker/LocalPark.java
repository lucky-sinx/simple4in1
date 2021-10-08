package simpleworker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LocalPark extends Remote {
    void create(String host, int port, String name) throws RemoteException;
}
