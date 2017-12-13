package model;

import java.io.Serializable;

public class Server implements Serializable {
    protected String ip;
    protected int port;

    public Server() {
        ip = null;
        port = -1;
    }

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Server{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
