package com.zju.fourinone;

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
    }

    public static void startWorkerTimerTask(ParkRemote parkRemote, String workerHost, int workerPost, String workerName) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    parkRemote.heartbeat(workerHost, workerPost, workerName);
                } catch (RemoteException e) {
                    LogUtil.severe("[TimerUtil] " + "[startWorkerTimerTask] " + e.getClass()+": " + e.getMessage());
                }
            }
        }, 0, Config.getHeartbeatTime());
    }
}
