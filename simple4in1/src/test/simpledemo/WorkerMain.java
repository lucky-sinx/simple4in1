package test.simpledemo;

import simple4in1.Contractor;
import simple4in1.WorkerLocal;
import simple4in1.WareHouse;
import simple4in1.Worker;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.Scanner;

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

    @Test
    public void testcontractor2() throws RemoteException {
        MyCT ct = new MyCT();
        ct.giveTask();
    }
}

class MyWK extends Worker {
    @Override
    public WareHouse doTask(WareHouse input) {
        System.out.println("=====================================");
        System.out.println("input:" + input.toString());
        System.out.println("请输入一个字符：");
        Scanner sc = new Scanner(System.in);
        char c;
        c = sc.next().charAt(0);//读取字符串的第一个字母
        System.out.println("第一个字符是" + c);
        System.out.println("=====================================");
        return new WareHouse("key", c);
    }
}

class MyCT extends Contractor {
    @Override
    public void giveTask() throws RemoteException {
        WorkerLocal[] workers = getWaitingWorkers("test");
        WareHouse input = new WareHouse("key", "hello");
        for (WorkerLocal worker : workers) {
            WareHouse output = worker.doTask(input);
            System.out.println("等待工人结果返回");
            while (true) {
                if (output.getStatus() == WareHouse.READY) {
                    System.out.println("Worker 执行完成：");
                    System.out.println(output);
                    break;
                } else if (output.getStatus() == WareHouse.EXCEPTION) {
                    System.out.println("Worker 执行失败");
                    break;
                }
            }
        }
    }
}