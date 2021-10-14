package com.zju.fourinone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 理解:
 * = Fttp*
 */
public class FileSystem extends Service implements LocalFileSystem {
    public FileSystem(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    @Override
    public boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    @Override
    public boolean isFile(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }

    @Override
    public boolean isDirectory(String filePath) {
        File file = new File(filePath);
        return file.isDirectory();
    }

    @Override
    public boolean isHidden(String filePath) {
        File file = new File(filePath);
        return file.isHidden();
    }

    @Override
    public boolean isAbsolute(String filePath) {
        File file = new File(filePath);
        return file.isAbsolute();
    }

    @Override
    public boolean canRead(String filePath) {
        File file = new File(filePath);
        return file.canRead();
    }

    @Override
    public boolean canWrite(String filePath) {
        File file = new File(filePath);
        return file.canWrite();
    }

    @Override
    public boolean canExecute(String filePath) {
        File file = new File(filePath);
        return file.isAbsolute();
    }

    @Override
    public long lastModified(String filePath) {
        File file = new File(filePath);
        return file.lastModified();
    }

    @Override
    public long length(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    @Override
    public boolean delete(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    @Override
    public boolean mkdir(String filePath) {
        File file = new File(filePath);
        return file.mkdir();
    }

    @Override
    public String read(String filePath) {
        File file = new File(filePath);
        StringBuilder res = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1];
            while (fileInputStream.read(buffer) != -1) {
                res.append(new String(buffer));
            }
            fileInputStream.close();
        } catch (IOException e) {
            LogUtil.severe(String.format("[FileSystem] [read] FileSystem(%s:%s:%s) read file(%s) fail.\n%s",
                    getHost(), getPort(), getName(), filePath, e.getMessage()));
        }
        return res.toString();
    }

    @Override
    public void write(String filePath, String content) {
        try {
            File file = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            LogUtil.severe(String.format("[FileSystem] [write] FileSystem(%s:%s:%s) write file(%s) fail with content(%s).\n%s",
                    getHost(), getPort(), getName(), filePath, content, e.getMessage()));
        }
    }
}
