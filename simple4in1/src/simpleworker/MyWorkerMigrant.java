package simpleworker;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MyWorkerMigrant {
    String host;
    String workerName;
    int port;

    public String getHost() {
        return host;
    }

    public String getWorkerName() {
        return workerName;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void doTask() {
        System.out.println("My WorkerService: DoTask");
    }


    public void startService(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.workerName = name;
//        System.out.println(this.getClass());
        DynamicProxy dynamicProxy = new DynamicProxy(new MyWorkerService(this));
        MyWorker myworker = (MyWorker) dynamicProxy.bind(MyWorker.class);
        RMIService.createService(host, port, name, myworker);

        //获取ParkService
        try {

            LocalPark localPark = (LocalPark) RMIService.getService("localhost", 2000, "ParkService");
            localPark.create(this.host, this.port, this.workerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
