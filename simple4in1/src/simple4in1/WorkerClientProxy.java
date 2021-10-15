package simple4in1;

import java.rmi.RemoteException;

public class WorkerClientProxy {
    LocalWorker worker;

    public WorkerClientProxy(LocalWorker worker) {
        this.worker = worker;
    }

    public WareHouse doTask(WareHouse input) throws RemoteException {
        //NOTREADY为0
        final WareHouse output = new WareHouse(WareHouse.NOTREADY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WareHouse result = worker.doTask(input);
//                    System.out.println("result:" + result);
                    output.putAll(result);
                    output.setStatus(WareHouse.READY);
                } catch (Exception e) {
                    output.setStatus(WareHouse.EXCEPTION);
                }
            }
        }).start();
        System.out.println("多线程代理远程worker");
        return output;
    }
}
