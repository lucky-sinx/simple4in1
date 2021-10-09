package com.zju.fourinone;


import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkerService implements Serializable {
    Worker worker;
    private final Lock lk = new ReentrantLock();

    public WorkerService(Worker worker) {
        this.worker = worker;
    }

    public void doTask() {
        try {
            this.lk.lock();
            this.worker.doTask();
        } finally {
            this.lk.unlock();
        }
    }
}
