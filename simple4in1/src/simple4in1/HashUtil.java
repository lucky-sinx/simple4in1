package simple4in1;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashUtil {
    //采用一致性哈希算法选择缓存节点
    private final SortedMap<Long, String> hashCircle = new TreeMap<Long, String>();

    public long hash_md5(String key) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.reset();
        md5.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] byte_key = md5.digest();
        long res = ((long) (byte_key[3] & 0xFF) << 24) |
                ((long) (byte_key[2] & 0xFF) << 16 |
                        ((long) (byte_key[1] & 0xFF) << 8)
                        | (long) (byte_key[0] & 0xFF));
        return res & 0xffffffffL;
    }

    public long hash_code(String key) {
        return key.hashCode() & 0xffffffffL;
    }

    public void add(String node) {
        hashCircle.put(hash_md5(node), node);
    }

    public String get(String key) {
        if (hashCircle.isEmpty()) {
            return null;
        }
        long hashValue = hash_md5(key);
//        long hashValue = hash_code(key);
        if (hashCircle.containsKey(hashValue)) {
            return hashCircle.get(hashValue);
        } else {
            SortedMap<Long, String> longStringSortedMap = hashCircle.tailMap(hashValue);
            hashValue = longStringSortedMap.isEmpty() ? hashCircle.firstKey() : longStringSortedMap.firstKey();
            return hashCircle.get(hashValue);
        }
    }

    public void remove(String node) {
        hashCircle.remove(hash_md5(node));
    }

    public long getSize() {
        return hashCircle.size();
    }
}
