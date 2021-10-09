package simpleworker;

import java.io.Serializable;

public class WorkerMigrant implements Serializable {
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

    @Override
    public String toString() {
        return "MyWorkerMigrant{" +
                "host='" + host + '\'' +
                ", workerName='" + workerName + '\'' +
                ", port=" + port +
                '}';
    }

    public void startService(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.workerName = name;
//        System.out.println(this.getClass());
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerService(this));
//        MyWorker myworker = (MyWorker) dynamicProxy.bind(MyWorker.class);
        LocalWorker myworker = (LocalWorker) dynamicProxy.bind(LocalWorker.class);
        RMIService.createService(host, port, name, myworker);

        //获取ParkService
        try {

            LocalPark localPark = (LocalPark) RMIService.getService("localhost", 2001, "ParkService");
            localPark.create(this.host, this.port, this.workerName);
            System.out.println(localPark.get(this.workerName));
//            localPark.f(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
