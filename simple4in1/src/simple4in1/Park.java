package simple4in1;

import javax.crypto.interfaces.PBEKey;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Park extends Service implements ParkLocal {
    private final Map<String, Map<String, Map<String, Object>>> workers = new HashMap<>();
    private final Map<String, Object> fileServer = new HashMap<>();
    private final Map<String, Object> cacheServer = new HashMap<>();
    private final static long timeout = Config.getHeartbeatTime() * 2;
    private final ReadWriteLock rwlk = new ReentrantReadWriteLock();

    public Park(String host, int port, String name) {
        this.setHost(host);
        this.setPort(port);
        this.setName(name);
        LogUtil.info("Park start");
    }

    public void createWorker(String host, int port, String name) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
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
        LogUtil.info(String.format("[Worker]%s create", host + ":" + port + ":" + name));
        wlk.unlock();
    }

    public void createFileServer(String fileServerHost) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
//        Object node = fileServer.get(fileServerHost);
        fileServer.put(fileServerHost, System.currentTimeMillis());
        LogUtil.info(String.format("[FileServer]%s create", fileServerHost));
        wlk.unlock();
    }

    public void createCacheServer(String cacheServerId) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
//        Object node = fileServer.get(fileServerHost);
        cacheServer.put(cacheServerId, System.currentTimeMillis());
        LogUtil.info(String.format("[CacheServer]%s create", cacheServerId));
        wlk.unlock();
    }


    public void heartbeatworker(String host, int port, String name) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        Map<String, Map<String, Object>> node = workers.get(name);
        if (node == null) {
            LogUtil.warning("[Park] [heartbeat] workers get node " + name + " null");
        } else {
            Map<String, Object> info = node.get(host + ":" + port + ":" + name);
            if (info == null) {
                LogUtil.warning("[Park] [heartbeat] node get info " + host + ":" + port + ":" + name + " null");
            } else {
                info.put("time", System.currentTimeMillis());
            }
        }
        wlk.unlock();
    }

    public void heartbeatfileserver(String fileServerHost) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        Object node = fileServer.get(fileServerHost);
//        System.out.println(node);
        if (node == null) {
            LogUtil.warning("[Park] [heartbeat] file server get node " + fileServerHost + " null");
        } else {
            fileServer.put(fileServerHost, System.currentTimeMillis());
        }
        wlk.unlock();
    }

    public void heartbeatcacheserver(String cacheServerId) {
        Lock wlk = rwlk.writeLock();
        wlk.lock();
        Object node = cacheServer.get(cacheServerId);
//        System.out.println(node);
        if (node == null) {
            LogUtil.warning("[Park] [heartbeat] cache server get node " + cacheServerId + " null");
        } else {
            cacheServer.put(cacheServerId, System.currentTimeMillis());
        }
        wlk.unlock();
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
                    LogUtil.warning(String.format("[Worker]%s remove", next2.getKey()));
                }
            }
        }
        Iterator<Map.Entry<String, Object>> iterator3 = fileServer.entrySet().iterator();
        while (iterator3.hasNext()) {
            Map.Entry<String, Object> next = iterator3.next();
            if (System.currentTimeMillis() - (Long) next.getValue() > timeout) {
                iterator3.remove();
                LogUtil.warning(String.format("[FileServer]%s remove", next.getKey()));
            }
        }
        Iterator<Map.Entry<String, Object>> iterator4 = cacheServer.entrySet().iterator();
        while (iterator4.hasNext()) {
            Map.Entry<String, Object> next = iterator4.next();
            if (System.currentTimeMillis() - (Long) next.getValue() > timeout) {
                iterator4.remove();
                LogUtil.warning(String.format("[CacheServer]%s remove", next.getKey()));
            }
        }
    }

    public Map<String, Map<String, Object>> get(String name) {
        Lock rlk = rwlk.readLock();
        Map<String, Map<String, Object>> res;
        rlk.lock();
        res = workers.get(name);
        rlk.unlock();
        return res;
    }

    public static void main(String[] args) {
        RMIService.startPark();
    }
}