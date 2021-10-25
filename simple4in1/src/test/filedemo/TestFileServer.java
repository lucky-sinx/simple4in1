package test.filedemo;

import simple4in1.LocalFileSystem;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class TestFileServer {
    public static void main(String[] args) throws RemoteException {
        LocalFileSystem remoteServer = RMIService.getFileServer("localhost");
        System.out.println(remoteServer.exists("config.xml"));
    }
}
