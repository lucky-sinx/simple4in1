package com.zju.fourinone;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 理解：
 * =Worker*
 */
public class Worker extends Service implements WorkerRemote {
    private final Lock lk = new ReentrantLock();

    public Worker(String host, int port, String name) {
        super(host, port, name);
    }

    @Override
    public void doTask() {
        try {
            this.lk.lock();
            System.out.println("Here>>>>");
        } finally {
            this.lk.unlock();
        }
    }

    @Override
    public String toString() {
        return "MyWorkerMigrant{" +
                "host='" + getHost() + '\'' +
                ", workerName='" + getName() + '\'' +
                ", port=" + getPort() +
                '}';
    }
}
