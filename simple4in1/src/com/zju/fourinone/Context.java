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

    static LocalPark getPark() {
        return (LocalPark) RegistryUtil.getRegistry(Config.getParkHost(), Config.getParkPort(), Config.getParkName());
    }

    static LocalWorker getWorker(String host, int port, String workerName) {
        return (LocalWorker) RegistryUtil.getRegistry(host, port, workerName);
    }

    static void startWorker(Worker worker) {
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
        LocalWorker myworker = (LocalWorker) dynamicProxy.bind(LocalWorker.class);
        RegistryUtil.createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {
            LocalPark localPark = getPark();
            localPark.create(worker.getHost(), worker.getPort(), worker.getName());
            LocalPark parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.create(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(localPark.get(worker.getName()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
        }
    }
}
