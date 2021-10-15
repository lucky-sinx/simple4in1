package simple4in1;


import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerServiceProxy implements Serializable {
    Worker worker;
    private final Lock lk = new ReentrantLock();

    public WorkerServiceProxy(Worker worker) {
        this.worker = worker;
    }

    public WareHouse doTask(WareHouse input) {
        WareHouse output = new WareHouse();
        try {
            this.lk.lock();
            output = this.worker.doTask(input);
        } finally {
            this.lk.unlock();
        }
        return output;
    }
}
