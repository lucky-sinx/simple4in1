package com.zju.fourinone;

public abstract class Service {
    private final String host;
    private final int port;
    private final String name;

    public Service(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }
}
