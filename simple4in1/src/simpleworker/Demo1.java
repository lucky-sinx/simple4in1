package simpleworker;

import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Demo1 {
    public static void main(String[] args) {
        Test01 test01 = new Test01();
        test01.startService("localhost", 8001, "test");
    }

    @Test
    public void testservice() throws MalformedURLException, NotBoundException, RemoteException {
//        try {
//            Registry registry = LocateRegistry.getRegistry("localhost", 8001);
//            MyWorker remoteMath = (MyWorker) registry.lookup("test");
//            remoteMath.doTask();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        MyWorker worker = (MyWorker) RMIService.getService("localhost", 8001, "test");
        worker.doTask();
    }
}

class Test01 extends MyWorkerMigrant {
    @Override
    public void doTask() {
        System.out.println("Test01:DoTask");
        for (int i = 0; i < 3; i++) {
            printhello();
        }
        System.out.println("=====================");
    }

    public void printhello() {
        System.out.println("hello");
    }
}
