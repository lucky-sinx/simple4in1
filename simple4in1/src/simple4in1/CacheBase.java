package simple4in1;

public class CacheBase implements Comparable<CacheBase> {
    private Object key;
    private Object value;
    private long lastTime;
    private long writeTime;
    private Integer count;

    public void setKey(Object key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public int compareTo(CacheBase o) {
        return count.compareTo(o.count);
    }
}
