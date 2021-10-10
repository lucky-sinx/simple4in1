package com.zju.fourinone;

import java.rmi.RemoteException;

public class WorkerClientProxy {
    LocalWorker worker;

    public WorkerClientProxy(LocalWorker worker) {
        this.worker = worker;
    }

    public void doTask() throws RemoteException {
        System.out.println("多线程代理远程worker");
        worker.doTask();
    }
}
