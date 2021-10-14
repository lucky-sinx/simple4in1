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
public class FileSystemLB extends FileSystem {
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
        super(host, port, name);
    }

    @Override
    public boolean exists(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.EXISTS, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean isFile(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.ISFILE, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean isDirectory(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.ISDIRECTORY, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean isHidden(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.ISHIDDEN, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean isAbsolute(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.ISABSOLUTE, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean canRead(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.CANREAD, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean canWrite(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.CANWRITE, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean canExecute(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.CANEXECUTE, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public long lastModified(String filePath) {
        Long res = (Long) invoke(FileOperation.LASTMODIFIED, new Object[]{filePath});
        if (res == null) {
            return 0;
        }
        return res;
    }

    @Override
    public long length(String filePath) {
        Long res = (Long) invoke(FileOperation.LENGTH, new Object[]{filePath});
        if (res == null) {
            return 0;
        }
        return res;
    }

    @Override
    public boolean delete(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.DELETE, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public boolean mkdir(String filePath) {
        Boolean res = (Boolean) invoke(FileOperation.MKDIR, new Object[]{filePath});
        if (res == null) {
            return false;
        }
        return res;
    }

    @Override
    public String read(String filePath) {
        String res = (String) invoke(FileOperation.READ, new Object[]{filePath});
        if (res == null) {
            return "";
        }
        return res;
    }

    @Override
    public void write(String filePath, String content) {
        invoke(FileOperation.WRITE, new Object[]{filePath, content});
        synchronize(FileOperation.WRITE, new Object[]{filePath, content});
    }

    /**
     * FileSystem远程调用
     *
     * @param fileOperation: 文件操作
     * @param args:          远程调用方法的参数（包括文件路径filePath和写文件内容content）
     */
    private Object invoke(FileOperation fileOperation, Object[] args) {
        String[] fileSystem = fileSystems.get(nextFileSystemIndex).split(":");
        int port = Integer.parseInt(fileSystem[1]);
        String name = fileSystem[2];
        nextFileSystemIndex = (nextFileSystemIndex + 1) % fileSystems.size();

        LocalFileSystem fileSystemRemote;
        try {
            fileSystemRemote = (LocalFileSystem) RegistryUtil.getRegistry(port, name);
        } catch (NotBoundException | RemoteException e) {
            LogUtil.severe(String.format("[FileSystemLB] [invoke] connect FileSystem(%s:%s:%s) fail.\n%s",
                    fileSystem[0], fileSystem[1], fileSystem[2], e.getMessage()));
            return null;
        }

        Class<?>[] params = new Class[args.length];
        Arrays.fill(params, String.class);
        try {
            return fileSystemRemote.getClass()
                    .getMethod(fileOperation.name, params)
                    .invoke(fileSystemRemote, args);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            LogUtil.severe(String.format("[FileSystemLB] [invoke] remote invoke FileSystem(%s:%s:%s) method(%s) fail\n%s",
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
            LocalFileSystem fileSystemRemote;
            try {
                fileSystemRemote = (LocalFileSystem) RegistryUtil.getRegistry(port, name);
            } catch (NotBoundException | RemoteException e) {
                LogUtil.severe(String.format("[FileSystemLB] [synchronize] connect FileSystem(%s:%s:%s) fail.\n%s",
                        splits[0], splits[1], splits[2], e.getMessage()));
                continue;
            }
            Class<?>[] params = new Class[args.length];
            Arrays.fill(params, String.class);
            try {
                fileSystemRemote.getClass()
                        .getMethod(fileOperation.name, params)
                        .invoke(fileSystemRemote, args);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                LogUtil.severe(String.format("[FileSystemLB] [synchronize] remote invoke FileSystem(%s:%s:%s) method(%s) fail\n%s",
                        splits[0], splits[1], splits[2], fileOperation.name, e.getMessage()));
            }
        }
    }
}