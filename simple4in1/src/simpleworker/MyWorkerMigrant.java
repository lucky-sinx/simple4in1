package simpleworker;

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
        MyWorker myworker = (MyWorker) dynamicProxy.bind();
        RMIService.createService(host, port, name, myworker);
//        try {
//            myworker.doTask();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
