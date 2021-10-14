package com.zju.fourinone;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 使用Timer定时器工具
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

    public static void startWorkerTimerTask(LocalPark parkRemote, String workerHost, int workerPost, String workerName) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    parkRemote.heartbeat(workerHost, workerPost, workerName);
                } catch (RemoteException e) {
                    LogUtil.severe(String.format("[TimerUtil] [startWorkerTimerTask] remote invoke Park(can not get info) fail \n%s",
                            e.getMessage()));
                }
            }
        }, 0, Config.getHeartbeatTime());
    }
}
