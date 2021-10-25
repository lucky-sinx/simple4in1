package test.filedemo;

import simple4in1.FileServer;
import simple4in1.LocalFileSystem;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class StartFileServer {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer("192.168.10.1");
        fileServer.startFileServer();
    }
}
