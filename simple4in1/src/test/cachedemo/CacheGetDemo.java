package test.cachedemo;

import simple4in1.LocalCacheServer;
import simple4in1.ParkLocal;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class CacheGetDemo {
    public static void main(String[] args) throws RemoteException {
        LocalCacheServer localCacheServer = new LocalCacheServer();
        for (Integer i = 0; i < 30; i++) {
            System.out.println(localCacheServer.get(i.toString()));
        }
//        System.out.println(parkLocal.getCache("1"));
//        System.out.println(parkLocal.getCache("2"));
//        System.out.println(parkLocal.getCache("3"));
//        System.out.println(parkLocal.getCache("4"));
//        System.out.println(parkLocal.getCache("5"));
//        System.out.println(parkLocal.getCache("6"));
//        System.out.println(parkLocal.getCache("7"));
//        System.out.println(parkLocal.getCache("8"));
//        System.out.println(parkLocal.getCache("9"));
    }
}
