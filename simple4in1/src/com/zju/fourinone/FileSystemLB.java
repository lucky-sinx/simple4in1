package com.zju.fourinone;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * FileSystem负载均衡
 * 理解:
 * = Fttp*
 * 缺少文件锁
 */
public class FileSystemLB extends Service implements LocalLB {
    private final List<String> fileSystems = Config.getFileSystemServers();
    private int nextFileSystemIndex = 0;

    private enum FileOperation {
        EXISTS("exists"),
        ISFILE("isFile"),
        ISDIRECTORY("isDirectory"),
        ISHIDDEN("isHidden"),
        ISABSOLUTE("isAbsolute"),
        CANREAD("canRead"),
        CANWRITE("canWrite"),
        CANEXECUTE("canExecute"),
        LASTMODIFIED("lastModified"),
        LENGTH("length"),
        DELETE("delete"),
        MKDIR("mkdir"),

        READ("read"),
        WRITE("write");

        private final String name;

        FileOperation(String name) {
            this.name = name;
        }

    }

    public FileSystemLB(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    /**
     * FileSystem远程调用
     *
     * @param fileOperation: 文件操作（该接口不提供write操作）
     * @param filePath:      文件路径
     * @return 操作文件的结果
     */
    public Object invoke(FileOperation fileOperation, String filePath) {
        if (!fileOperation.equals(FileOperation.WRITE)) {
            LogUtil.warning("[FileSystemLB] [invoke] fileOperation(write) needs param(content).");
            return null;
        }
        return invoke(fileOperation, new Object[]{filePath});
    }

    /**
     * FileSystem远程调用
     *
     * @param fileOperation: 文件操作（该接口仅提供write操作）
     * @param filePath:      文件路径
     * @param content:       文件需要写的内容
     */
    public void invoke(FileOperation fileOperation, String filePath, String content) {
        if (!fileOperation.equals(FileOperation.WRITE)) {
            LogUtil.warning(String.format("[FileSystemLB] [invoke] fileOperation(%s) does not support param(content).",
                    fileOperation.name));
            return;
        }
        invoke(fileOperation, new Object[]{filePath, content});
        synchronize(fileOperation, new Object[]{filePath, content});
    }

    private Object invoke(FileOperation fileOperation, Object[] args) {
        String[] fileSystem = fileSystems.get(nextFileSystemIndex).split(":");
        int port = Integer.parseInt(fileSystem[1]);
        String name = fileSystem[2];
        nextFileSystemIndex = (nextFileSystemIndex + 1) % fileSystems.size();

        LocalFileSystem localFileSystem;
        try {
            localFileSystem = (LocalFileSystem) RegistryUtil.getRegistry("localhost",port, name);
        } catch (Exception e) {
            LogUtil.severe(String.format("[FileSystemLB] [invoke] connect FileSystem(%s:%s:%s) fail.\n%s",
                    fileSystem[0], fileSystem[1], fileSystem[2], e.getMessage()));
            return null;
        }

        Class<?>[] params = new Class[args.length];
        Arrays.fill(params, String.class);
        try {
            return localFileSystem.getClass()
                    .getMethod(fileOperation.name, params)
                    .invoke(localFileSystem, args);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            LogUtil.severe(String.format("[FileSystemLB] [invoke] invoke FileSystem(%s:%s:%s) method(%s) fail\n%s",
                    fileSystem[0], fileSystem[1], fileSystem[2], fileOperation.name, e.getMessage()));
            return null;
        }
    }

    /**
     * 文件同步
     *
     * @param fileOperation: 文件操作（目前仅支持write操作）
     * @param args:          远程调用方法的参数（包括文件路径filePath和写文件内容content）
     */
    private void synchronize(FileOperation fileOperation, Object[] args) {
        if (!fileOperation.equals(FileOperation.WRITE)) {
            LogUtil.warning(String.format("[FileSystemLB] [synchronize] fileOperation(%s) does not support synchronize.",
                    fileOperation.name));
            return;
        }
        for (String fileSystem : fileSystems) {
            String[] splits = fileSystem.split(":");
            int port = Integer.parseInt(splits[1]);
            String name = splits[2];
            LocalFileSystem localFileSystem;
            try {
                localFileSystem = (LocalFileSystem) RegistryUtil.getRegistry("localhost",port, name);
            } catch (Exception e) {
                LogUtil.severe(String.format("[FileSystemLB] [synchronize] connect FileSystem(%s:%s:%s) fail.\n%s",
                        splits[0], splits[1], splits[2], e.getMessage()));
                continue;
            }
            Class<?>[] params = new Class[args.length];
            Arrays.fill(params, String.class);
            try {
                localFileSystem.getClass()
                        .getMethod(fileOperation.name, params)
                        .invoke(localFileSystem, args);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                LogUtil.severe(String.format("[FileSystemLB] [synchronize] invoke FileSystem(%s:%s:%s) method(%s) fail\n%s",
                        splits[0], splits[1], splits[2], fileOperation.name, e.getMessage()));
            }
        }
    }
}