package com.zju.fourinone;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class WorkerMain {
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
//        Context.startWorker("helloWork");
//        MigrantWorker worker = new MigrantWorker();
//        worker.waitWorking("hello");
        MyWK wk = new MyWK();
        wk.startWorker("localhost", 8001, "test");
    }
}

class MyWK extends Worker {
    @Override
    public void doTask() {
        System.out.println("hello");
    }
}