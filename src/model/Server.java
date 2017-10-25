package model;

import java.io.Serializable;

public class Server implements Serializable{
    private String ip;
    private int port;

    public Server() {
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
}
