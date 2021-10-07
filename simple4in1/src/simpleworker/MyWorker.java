package simpleworker;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyWorker extends Remote {
    void doTask() throws RemoteException;
}
