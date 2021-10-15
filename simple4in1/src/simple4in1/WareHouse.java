package simple4in1;

import java.util.LinkedHashMap;

public class WareHouse extends LinkedHashMap {
    public final static int NOTREADY = 0;
    public final static int READY = 1;
    public final static int EXCEPTION = -1;
    int status = READY;
    boolean mark = true;

    public WareHouse() {
        super();
    }

    public WareHouse(int status) {
        super();
        this.status = status;
    }

    public WareHouse(Object k, Object v) {
        super();
        this.put(k, v);
    }

    public int getStatus() {
        return status;
    }

    synchronized void setStatus(int status) {
        this.status = status;
    }

    public synchronized boolean isReady() {
        return this.status == READY;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public static void main(String[] args) {
        WareHouse wh = new WareHouse("key", "ok");
        //wh.put("bbb",new Bean("",99,new java.util.ArrayList()));
        System.out.println(wh.get("key"));
    }
}
