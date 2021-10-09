package simpleworker;

import java.rmi.RemoteException;

public class Contractor {
    public void giveTask() throws RemoteException {

    }


    public LocalWorker[] getWaitingWorkers(String workerName) {
        LocalWorker[] waitingWorkers = null;
        waitingWorkers = RMIService.getWaitingWorkers(workerName, RMIService.getPark());
        return waitingWorkers;
    }
}
