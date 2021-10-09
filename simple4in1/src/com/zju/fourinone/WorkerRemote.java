package com.zju.fourinone;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerRemote extends Remote {
    void doTask() throws RemoteException;
}
