package test.filedemo;

import org.junit.Test;
import simple4in1.FileServer;
import simple4in1.LocalFileSystem;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class TestFileServer {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer("192.168.10.1");
        fileServer.startFileServer();
    }

    @Test
    public void test_server() throws RemoteException {
        LocalFileSystem remoteServer = RMIService.getFileServer("localhost");
        System.out.println(remoteServer.exists("config.xml"));
    }
}
