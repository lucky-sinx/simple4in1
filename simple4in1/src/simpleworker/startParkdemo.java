package simpleworker;

import org.junit.Test;
import simpleworker.ParkService;

public class startParkdemo {
    public static void main(String[] args) {
        ParkService parkService = new ParkService();
        parkService.startPark("localhost", 2001, "a");
    }
}
