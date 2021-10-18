package test.cachedemo;

import simple4in1.ParkLocal;
import simple4in1.RMIService;

import java.rmi.RemoteException;

public class CachePutDemo {
    public static void main(String[] args) throws RemoteException {
        ParkLocal parkLocal = RMIService.getPark();
        for (Integer i = 0; i < 30; i++) {
            System.out.println(parkLocal.putCache(i.toString(), i));
        }
//        System.out.println(parkLocal.putCache("1", "123"));
//        System.out.println(parkLocal.putCache("2", "223"));
//        System.out.println(parkLocal.putCache("3", "321"));
//        System.out.println(parkLocal.putCache("4", "434"));
//        System.out.println(parkLocal.putCache("5", "554"));
//        System.out.println(parkLocal.putCache("6", "666"));
//        System.out.println(parkLocal.putCache("7", "765"));
//        System.out.println(parkLocal.putCache("8", "887"));
//        System.out.println(parkLocal.putCache("9", "998"));
    }
}
