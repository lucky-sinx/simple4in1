package simpleworker;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerService {
    WorkerMigrant migrant;
    private Lock lk = new ReentrantLock();

    public WorkerService(WorkerMigrant migrant) {
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
