package simple4in1;

import java.util.concurrent.ConcurrentHashMap;

public class Cache extends Service{
    public ConcurrentHashMap<String, CacheBase> concurrentHashMap = new ConcurrentHashMap<>();
}
