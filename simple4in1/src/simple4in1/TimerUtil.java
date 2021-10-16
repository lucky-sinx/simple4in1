package simple4in1;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 使用Timer定时器
 */
public class TimerUtil {
    private static final Timer timer = new Timer();

    public static void startParkTimerTask(Park park) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                park.checkHeartbeats();
            }
        }, 0, Config.getHeartbeatTime());
        LogUtil.info("ParkTimerTask start");
    }

    public static void startWorkerTimerTask(ParkLocal parkLocal, String workerHost, int workerPost, String workerName) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    parkLocal.heartbeatworker(workerHost, workerPost, workerName);
//                    LogUtil.info("WorkerTimerTask start");
                } catch (RemoteException e) {
                    LogUtil.severe("[TimerUtil] [startWorkerTimerTask] " + e.getClass()+": " + e.getMessage());
                }
            }
        }, 0, Config.getHeartbeatTime());
    }

    public static void startFileServerTimerTask(ParkLocal parkLocal, String fileServerHost){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    parkLocal.heartbeatfileserver(fileServerHost);
//                    LogUtil.info("WorkerTimerTask start");
                } catch (RemoteException e) {
                    LogUtil.severe("[TimerUtil] [startWorkerTimerTask] " + e.getClass()+": " + e.getMessage());
                }
            }
        }, 0, Config.getHeartbeatTime());
    }

    public static void startCacheServerTimerTask(ParkLocal parkLocal, String cacheServerHost){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    parkLocal.heartbeatcacheserver(cacheServerHost);
//                    LogUtil.info("WorkerTimerTask start");
                } catch (RemoteException e) {
                    LogUtil.severe("[TimerUtil] [startWorkerTimerTask] " + e.getClass()+": " + e.getMessage());
                }
            }
        }, 0, Config.getHeartbeatTime());
    }
}
