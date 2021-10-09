package com.zju.fourinone;

import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class WorkerMain {
    public static void main(String[] args) {
        MyWK wk = new MyWK();
        wk.startWorker("localhost", 8001, "test");
    }

    @Test
    public void testcontractor() throws RemoteException {
        MyCT ct = new MyCT();
        ct.giveTask();
    }
}

class MyWK extends Worker {
    @Override
    public void doTask() {
        System.out.println("=====================================");
        System.out.println(this + "(" + this.getClass().toString() + "):DoTask");
        System.out.println("=====================================");
    }
}

class MyCT extends Contractor {
    @Override
    public void giveTask() throws RemoteException {
        LocalWorker[] workers = getWaitingWorkers("test");
        for (LocalWorker worker : workers) {
            worker.doTask();
        }
    }
}