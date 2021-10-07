package simpleworker;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyWorkerService {
    MyWorkerMigrant migrant;
    private Lock lk = new ReentrantLock();

    public MyWorkerService(MyWorkerMigrant migrant) {
        this.migrant = migrant;
    }

    public void doTask() {
        try {
//            System.out.println("Here>>>>");
            this.lk.lock();
            this.migrant.doTask();
        } finally {
            this.lk.unlock();
        }
    }
}
