package simple4in1;

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

    protected static WorkerLocal[] getWaitingWorkers(String workerName) {
        Map<String, Map<String, Object>> workersInfo;
        WorkerLocal[] waitingWorkers = null;
        try {
            workersInfo = Context.getPark().get(workerName);
            if (workersInfo == null) {
                LogUtil.warning("[Contractor] [getWaitingWorkers] getPark null");
            } else {
                waitingWorkers = new WorkerLocal[workersInfo.size()];
                int index = 0;
                Iterator<Map.Entry<String, Map<String, Object>>> iterator = workersInfo.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Map<String, Object>> next = iterator.next();
                    Map<String, Object> info = next.getValue();
                    WorkerLocal serverWorker = Context.getWorker((String) info.get("host"), (Integer) info.get("port"), (String) info.get("name"));
                    DynamicProxy dynamicProxy = new DynamicProxy(new WorkerClientProxy(serverWorker));
                    WorkerLocal clientworker = (WorkerLocal) dynamicProxy.bind(WorkerLocal.class);
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
