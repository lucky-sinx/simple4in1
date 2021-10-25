package simple4in1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerLocal extends Remote {
    public WareHouse doTask(WareHouse input) throws RemoteException;
}
