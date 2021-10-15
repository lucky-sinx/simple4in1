package simple4in1;

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
        RMIUtil.createRegistry(port, name, park);
        TimerUtil.startParkTimerTask(park);
    }

    static ParkLocal getPark() {
        return (ParkLocal) RMIUtil.getRegistry(Config.getParkHost(), Config.getParkPort(), Config.getParkName());
    }

    static WorkerLocal getWorker(String host, int port, String workerName) {
        return (WorkerLocal) RMIUtil.getRegistry(host, port, workerName);
    }

    static void startWorker(Worker worker) {
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
        WorkerLocal myworker = (WorkerLocal) dynamicProxy.bind(WorkerLocal.class);
        RMIUtil.createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {
            ParkLocal parkLocal = getPark();
            parkLocal.create(worker.getHost(), worker.getPort(), worker.getName());
            ParkLocal parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.create(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(parkLocal.get(worker.getName()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
        }
    }

    private static void startFileSystem() throws RemoteException {

    }
}
