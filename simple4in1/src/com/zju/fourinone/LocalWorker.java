package com.zju.fourinone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LocalWorker extends Remote {
    public WareHouse doTask(WareHouse input) throws RemoteException;
}
