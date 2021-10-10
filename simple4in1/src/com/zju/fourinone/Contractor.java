package com.zju.fourinone;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

/**
 * 理解：
 * =Contractor*
 */
public class Contractor {
    public void giveTask() throws RemoteException {

    }

    public WareHouse giveTask(WareHouse input) throws RemoteException{
        return null;
    }

    protected static LocalWorker[] getWaitingWorkers(String workerName) {
        Map<String, Map<String, Object>> workersInfo;
        LocalWorker[] waitingWorkers = null;
        try {
            workersInfo = Context.getPark().get(workerName);
            if (workersInfo == null) {
                LogUtil.warning("[Contractor] [getWaitingWorkers] getPark null");
            } else {
                waitingWorkers = new LocalWorker[workersInfo.size()];
                int index = 0;
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = workersInfo.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Map<String, Object>> next = iterator.next();
                    Map<String, Object> info = next.getValue();
                    LocalWorker serverWorker = Context.getWorker((String) info.get("host"), (Integer) info.get("port"), (String) info.get("name"));
                    DynamicProxy dynamicProxy = new DynamicProxy(new WorkerClientProxy(serverWorker));
                    LocalWorker clientworker = (LocalWorker) dynamicProxy.bind(LocalWorker.class);
                    waitingWorkers[index] = clientworker;
                    index += 1;
                }
            }
        } catch (RemoteException e) {
            LogUtil.severe("[Contractor] [getWaitingWorkers] " + e.getClass() + e.getMessage());
        }
        return waitingWorkers;
    }
}
