package com.zju.fourinone;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 理解：
 * =BeanContext, ServiceContext, BeanService
 * Park, Worker and Contractor == Spring Bean
 */
public class Context {
    public static void startPark() {
        String name = Config.getParkName();
        String host = Config.getParkHost();
        int port = Config.getParkPort();
        Park park = new Park(host, port, name);
        RegistryUtil.createRegistry(port, name, park);
        TimerUtil.startParkTimerTask(park);
    }

    static LocalPark getPark() {
        return (LocalPark) RegistryUtil.getRegistry(Config.getParkHost(), Config.getParkPort(), Config.getParkName());
    }

    static LocalWorker getWorker(String host, int port, String workerName) {
        return (LocalWorker) RegistryUtil.getRegistry(host, port, workerName);
    }

    static void startWorker(Worker worker) {
        DynamicProxy dynamicProxy = new DynamicProxy(new WorkerServiceProxy(worker));
        LocalWorker myworker = (LocalWorker) dynamicProxy.bind(LocalWorker.class);
        RegistryUtil.createRegistry(worker.getPort(), worker.getName(), myworker);
        //获取ParkService
        try {
            LocalPark localPark = getPark();
            localPark.create(worker.getHost(), worker.getPort(), worker.getName());
            LocalPark parkRemote = getPark();
            if (parkRemote != null) {
                try {
                    parkRemote.create(worker.getHost(), worker.getPort(), worker.getName());
                } catch (RemoteException e) {
                    LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
                }
                TimerUtil.startWorkerTimerTask(parkRemote, worker.getHost(), worker.getPort(), worker.getName());
            }
            System.out.println(localPark.get(worker.getName()));
        } catch (RemoteException e) {
            LogUtil.severe("[Context] [startWorker] " + e.getClass() + ": " + e.getMessage());
        }
    }

    private static void startWeb() throws IOException {
        String host = Config.getWebHost();
        int port = Config.getWebPort();
        try {
            HttpUtil.startHttpServer(host, port, 0);
            LogUtil.info("Web has been started");
        } catch (IOException e) {
            LogUtil.severe(String.format("[Context] [startWeb] Web(%s%s) start fail.\n%s",
                    host, port, e.getMessage()));
            throw e;
        }
    }

    public static FileSystemLB startFileSystem() throws RemoteException {
        String name = Config.getFileSystemLBName();
        String host = Config.getFileSystemLBHost();
        int port = Config.getFileSystemLBPort();
        FileSystemLB fileSystemLB = new FileSystemLB(host, port, name);
        try {
            RegistryUtil.createRegistry(port, name, fileSystemLB);
            LogUtil.info("FileSystemLB has been started");
        } catch (Exception e) {
            LogUtil.severe(String.format("[Context] [startFileSystem] FileSystemLB(%s:%s:%s) start fail.\n%s",
                    host, port, name, e.getMessage()));
            throw e;
        }

        List<String> fileSystems = Config.getFileSystemServers();
        for (String fileSystemInfo : fileSystems) {
            String[] splits = fileSystemInfo.split(":");
            String fileSystemHost = splits[0];
            int fileSystemPort = Integer.parseInt(splits[1]);
            String fileSystemName = splits[2];
            FileSystem fileSystem = new FileSystem(fileSystemHost, fileSystemPort, fileSystemName);
            try {
                RegistryUtil.createRegistry(fileSystemPort, fileSystemName, fileSystem);
                LogUtil.info(String.format("FileSystem %s has been started", fileSystemName));
            } catch (Exception e) {
                LogUtil.severe(String.format("[Context] [start] FileSystem(%s) createRegistry fail.\n%s",
                        fileSystemInfo, e.getMessage()));
                throw e;
            }
        }
        LogUtil.info("FileSystems all have been started.");
        LogUtil.info("FileSystem has been started.");
        return fileSystemLB;
    }
}
