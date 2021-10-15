package test.wordcountdemo;

import com.zju.fourinone.Context;
import com.zju.fourinone.LocalFileSystem;

import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws RemoteException {
        Context.startContext();
        LocalFileSystem fileSystem = Context.getFileSystem();
        System.out.println(fileSystem.exists(""));
    }
}
