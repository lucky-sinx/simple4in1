package com.zju.fourinone;

import java.rmi.RemoteException;

/**
 * 理解：
 * =BeanContext, ServiceContext, BeanService
 * Park, Worker and Contractor == Spring Bean
 */
public class Context {
    public static void startPark() {
        String name = Config.getParkName();
        String host = Config.getParkHost();
        int port = Config.getParkPort();
        Park park = new Park(host, port, name);
        RegistryUtil.createRegistry(port, name, park);
        TimerUtil.startParkTimerTask(park);
    }

    static ParkRemote getPark() {
        return (ParkRemote) RegistryUtil.getRegistry(Config.getParkHost(), Config.getParkPort(), Config.getParkName());
    }

    public static void startWorker(String name) {
        String host = Config.getWorkerHost();
        int port = Config.getWorkerPort();
        Worker worker = new Worker(host, port, name);
        RegistryUtil.createRegistry(port, name, worker);
        ParkRemote parkRemote = getPark();
        if (parkRemote != null) {
            try {
                parkRemote.create(host, port, name);
            } catch (RemoteException e) {
                LogUtil.severe("[Context] " + "[startWorker] " + e.getClass() + ": " + e.getMessage());
            }
            TimerUtil.startWorkerTimerTask(parkRemote, host, port, name);
        }
    }

    static WorkerRemote getWorker(String host, int port, String workerName) {
        return (WorkerRemote) RegistryUtil.getRegistry(host, port, workerName);
    }
}
