package test.cachedemo;

import simple4in1.ParkLocal;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class CacheGetDemo {
    public static void main(String[] args) throws RemoteException {
        ParkLocal parkLocal = RMIService.getPark();
        for (Integer i = 0; i < 30; i++) {
            System.out.println(parkLocal.getCache(i.toString()));
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
