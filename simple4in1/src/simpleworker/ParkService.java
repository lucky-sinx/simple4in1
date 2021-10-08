package simpleworker;


import java.util.LinkedHashMap;

public class ParkService {
    LinkedHashMap<String, Object> parkinfo;

    public void create(String host, int port, String name) {
        System.out.println("create success " + host + ":" + port + " :" + name);
    }

    public void startPark(String host, int port, String name) {
        DynamicProxy dynamicProxy = new DynamicProxy(this);
        LocalPark localPark = (LocalPark) dynamicProxy.bind(LocalPark.class);
        RMIService.createService("localhost", 2000, "ParkService", localPark);
    }
}
