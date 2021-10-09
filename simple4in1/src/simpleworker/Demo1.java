package simpleworker;

import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Demo1 {
    public static void main(String[] args) {
        Test01 test01 = new Test01();
        test01.startService("localhost", 8001, "test");
        Test01 test02 = new Test01();
        test02.startService("192.168.10.1", 8002, "test");
        Test01 test03 = new Test01();
        test03.startService("localhost", 8003, "test");
    }

    @Test
    public void testdoTask() throws MalformedURLException, NotBoundException, RemoteException {
        Worker worker = (Worker) RMIService.getService("192.168.10.1", 8002, "test");
        worker.doTask();
    }

    @Test
    public void testservice() {
        ParkService parkService = new ParkService();
        parkService.startPark("localhost", 2001, "a");
    }

    @Test
    public void testcontractor() throws RemoteException {
        MyCT ct = new MyCT();
        ct.giveTask();
    }
}

class Test01 extends WorkerMigrant {
    @Override
    public void doTask() {
        System.out.println("=====================================");
        System.out.println(this.toString()+"("+this.getClass().toString()+"):DoTask");
        System.out.println("=====================================");
    }

    public void printhello() {
        System.out.println("hello");
    }
}

class MyCT extends Contractor {
    @Override
    public void giveTask() throws RemoteException {
        Contractor contractor = new Contractor();
        LocalWorker[] workers = contractor.getWaitingWorkers("test");
        for (LocalWorker worker : workers) {
            worker.doTask();
        }
    }
}
