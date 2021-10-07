package simpleworker;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIService {

    static void createService(String host, int port, String name, Remote obj) {
        try {
            UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind(name, obj);
            System.out.println("Worker " + name + " server ready[" + host + ":" + port + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Remote getService(String host, int port, String name) throws MalformedURLException, NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        Remote remote = registry.lookup(name);
        return remote;
    }
}
