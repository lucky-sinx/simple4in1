package simpleworker;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

public interface LocalPark extends Remote {
    void create(String host, int port, String name) throws RemoteException;

//    Vector<String> get(String name);

    ArrayList<String> get(String name) throws RemoteException;

//    void f(MyWorkerMigrant myWorker) throws RemoteException;

}
