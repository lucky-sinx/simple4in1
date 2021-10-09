package com.zju.fourinone;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 理解：
 * =Contractor*
 */
public class Contractor {
    public void giveTask() {

    }

    static WorkerRemote[] getWaitingWorkers(String workerName) {
        Map<String, Map<String, Object>> workersInfo;
        WorkerRemote[] waitingWorkers = null;
        try {
            workersInfo = Context.getPark().get(workerName);
            if (workersInfo == null) {
                System.out.println("未找到worker");
            } else {
                waitingWorkers = new WorkerRemote[workersInfo.size()];
                int index = 0;
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = workersInfo.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Map<String, Object>> next = iterator.next();
                    Map<String, Object> info = next.getValue();
                    waitingWorkers[index] = Context.getWorker((String) info.get("host"), (Integer) info.get("port"), (String) info.get("name"));
                    waitingWorkers[index].doTask();
                    index += 1;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return waitingWorkers;
    }
}
