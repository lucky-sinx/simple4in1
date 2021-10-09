package com.zju.fourinone;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 理解：
 * =Park*
 */
// implements ParkInterface为什么去掉也可以反射???
public class Park extends Service implements ParkRemote {
    private final Map<String, Map<String, Map<String, Object>>> workers = new HashMap<>();
    private final static long timeout = Config.getHeartbeatTime() * 2;
    private final ReadWriteLock rwlk = new ReentrantReadWriteLock();

    public Park(String host, int port, String name) {
        this.setHost(host);
        this.setPort(port);
        this.setName(name);
    }

    public void create(String host, int port, String name) {
//        workers.put(host + ":" + port + "/" + name, System.currentTimeMillis());
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        try {
//            List<Map<String, Object>> node = (ArrayList<Map<String, Object>>) workers.get(name);
            Map<String, Map<String, Object>> node = workers.get(name);
            if (node == null) {
                node = new HashMap<>();
                Map<String, Object> info = new HashMap<>();
                info.put("host", host);
                info.put("port", port);
                info.put("name", name);
                info.put("time", System.currentTimeMillis());
                node.put(host + ":" + port + ":" + name, info);
                workers.put(name, node);
            } else {
                Map<String, Object> info = new HashMap<>();
                info.put("host", host);
                info.put("port", port);
                info.put("name", name);
                info.put("time", System.currentTimeMillis());
                node.put(host + ":" + port + ":" + name, info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wlk.unlock();
        }
    }

    public void heartbeat(String host, int port, String name) {
//        workers.put(host + ":" + port + "/" + name, System.currentTimeMillis());
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        try {
//            List<Map<String, Object>> node = (ArrayList<Map<String, Object>>) workers.get(name);
            Map<String, Map<String, Object>> node = workers.get(name);
            if (node == null) {
                LogUtil.info("Wrong");
            } else {
                Map<String, Object> info = node.get(host + ":" + port + ":" + name);
                if (info == null) {
                    LogUtil.info("Wrong");
                } else {
                    info.put("time", System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wlk.unlock();
        }
        LogUtil.info(host + ":" + port + "/" + name + " beat!");
    }

    public void checkHeartbeats() {
        Iterator<Map.Entry<String, Map<String, Map<String, Object>>>> iterator1 = workers.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry<String, Map<String, Map<String, Object>>> next1 = iterator1.next();
            Iterator<Map.Entry<String, Map<String, Object>>> iterator2 = next1.getValue().entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry<String, Map<String, Object>> next2 = iterator2.next();
                Map<String, Object> info = next2.getValue();
                if (System.currentTimeMillis() - (Long) info.get("time") > timeout) {
                    iterator2.remove();
                    LogUtil.info(next2.getKey() + " remove!!!");
                }
            }
        }
//        Iterator<Map.Entry<String, Long>> iterator = workers.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Long> workerInfo = iterator.next();
//            if (System.currentTimeMillis() - workerInfo.getValue() > timeout) {
//                iterator.remove();
//                LogUtil.info(workerInfo.getKey() + " remove!!!");
//            }
//        }
    }

    public Map<String, Map<String, Object>> get(String name) {
        Lock rlk = rwlk.readLock();
        Map<String, Map<String, Object>> res = null;
        rlk.lock();
        try {
            res = workers.get(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rlk.unlock();
        }
        return res;
    }
}