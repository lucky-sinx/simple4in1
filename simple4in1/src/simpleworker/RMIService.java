package simpleworker;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class RMIService {

    static void createService(String host, int port, String name, Remote obj) {
        try {
            UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind(name, obj);
            System.out.println("Worker " + name + " server ready[" + host + ":" + port + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Remote getService(String host, int port, String name) throws MalformedURLException, NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        Remote remote = registry.lookup(name);
        return remote;
    }

    static LocalPark getPark() {
        //Park参数通过配置文件指定
        LocalPark park = null;
        try {
            park = (LocalPark) getService("localhost", 2001, "ParkService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return park;
    }

    static LocalWorker getWorker(String host, int port, String workerName) {
        LocalWorker worker = null;
        try {
            worker = (LocalWorker) getService(host, port, workerName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return worker;
    }

    static LocalWorker[] getWaitingWorkers(String workerName, LocalPark park) {
        ArrayList<String> workersInfo;
        LocalWorker[] waitingWorkers = null;
        try {
            workersInfo = park.get(workerName);
            if (workersInfo == null) {
                System.out.println("未找到worker");
            } else {
                waitingWorkers = new LocalWorker[workersInfo.size()];
                int index = 0;
                for (String workerInfo : workersInfo) {
                    String[] info = workerInfo.split(":");
                    waitingWorkers[index] = getWorker(info[0], Integer.parseInt(info[1]), info[2]);
                    waitingWorkers[index].doTask();
                    index += 1;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return waitingWorkers;
    }

}
