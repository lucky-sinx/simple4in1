package simpleworker;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParkService {
    private final LinkedHashMap<String, Object> parkinfo;
    private final ReadWriteLock rwlk = new ReentrantReadWriteLock();

    public ParkService() {
        parkinfo = new LinkedHashMap<String, Object>();
    }

    public void create(String host, int port, String name) {
//        System.out.println("create success " + host + ":" + port + " :" + name);
//        parkinfo.put();
//        Vector<String> node = (Vector<String>) parkinfo.get("name");
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        try {
            ArrayList<String> node = (ArrayList<String>) parkinfo.get(name);

            if (node == null) {
                node = new ArrayList<String>();
                node.add(host + ":" + port + ":" + name);
                parkinfo.put(name, node);
            } else {
                node.add(host + ":" + port + ":" + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wlk.unlock();
        }

    }

//    public Vector<String> get(String name) {
//        return (Vector<String>) parkinfo.get(name);
//    }

    public ArrayList<String> get(String name) {
        Lock rlk = rwlk.readLock();
        ArrayList<String> res = null;
        rlk.lock();
        try {
            res = (ArrayList<String>) parkinfo.get(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rlk.unlock();
        }
        return res;
    }

    public void startPark(String host, int port, String name) {
        DynamicProxy dynamicProxy = new DynamicProxy(this);
        LocalPark localPark = (LocalPark) dynamicProxy.bind(LocalPark.class);
        RMIService.createService("localhost", port, "ParkService", localPark);
    }

//    public void f(MyWorkerMigrant myWorker){
//        System.out.println(myWorker.toString());
//    }
}
