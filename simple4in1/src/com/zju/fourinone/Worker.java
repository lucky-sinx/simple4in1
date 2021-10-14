package com.zju.fourinone;

import java.rmi.RemoteException;

/**
 * 理解：
 * =Worker*
 */
public class Worker extends Service implements LocalWorker {
    @Override
    public WareHouse doTask(WareHouse input) {
        return null;
    }

    @Override
    public String toString() {
        return "MyWorkerMigrant{" +
                "host='" + getHost() + '\'' +
                ", workerName='" + getName() + '\'' +
                ", port=" + getPort() +
                '}';
    }

    public void startWorker(String host, int port, String name) throws RemoteException {
        this.setHost(host);
        this.setPort(port);
        this.setName(name);
        Context.startWorker(this);
        LogUtil.info("Worker start");
    }
}
