package simple4in1;

import java.rmi.RemoteException;

public class LocalCacheServer implements CacheServerLocal {
    ParkLocal park = RMIService.getPark();

    @Override
    public boolean put(String key, Object value) throws RemoteException {
        return this.park.putCache(key, value);
    }

    @Override
    public Object get(String key) throws RemoteException {
        return this.park.getCache(key);
    }
}
