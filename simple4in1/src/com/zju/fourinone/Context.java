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

//    public static void startWorker(String name) {
//        String host = Config.getWorkerHost();
//        int port = Config.getWorkerPort();
//        Worker worker = new Worker(host, port, name);
//        RegistryUtil.createRegistry(port, name, worker);
//        ParkRemote parkRemote = getPark();
//        if (parkRemote != null) {
//            try {
//                parkRemote.create(host, port, name);
//            } catch (RemoteException e) {
//                LogUtil.severe("[Context] " + "[startWorker] " + e.getClass() + ": " + e.getMessage());
//            }
//            TimerUtil.startWorkerTimerTask(parkRemote, host, port, name);
//        }
//    }

    static WorkerRemote getWorker(String host, int port, String workerName) {
        return (WorkerRemote) RegistryUtil.getRegistry(host, port, workerName);
    }

    static void startWorker(Worker worker) {
//        System.out.println(this.getClass());
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerService(worker));
//        MyWorker myworker = (MyWorker) dynamicProxy.bind(MyWorker.class);
        WorkerRemote myworker = (WorkerRemote) dynamicProxy.bind(WorkerRemote.class);
//        RMIService.createService(host, port, name, myworker);
        RegistryUtil.createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {

            ParkRemote localPark = (ParkRemote) getPark();
            localPark.create(worker.getHost(), worker.getPort(), worker.getName());
            ParkRemote parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.create(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] " + "[startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(localPark.get(worker.getName()));
//            localPark.f(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
