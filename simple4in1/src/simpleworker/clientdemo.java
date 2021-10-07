package simpleworker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class clientdemo {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            MyWorker remoteMath = (MyWorker) registry.lookup("worker");
            remoteMath.doTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
