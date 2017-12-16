package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ApplicationServer extends Server {
    // Used by Dispatcher
    private Server assignedDbServer;
    private int newDatabaseIndex;

    public ApplicationServer(String ip, int port) {
        super(ip, port);
        this.assignedDbServer = null;
        newDatabaseIndex = 0;
    }

    public ApplicationServer(String ip, int port, Server assignedDbServer) {
        super(ip, port);
        this.assignedDbServer = assignedDbServer;
    }

    public Server getAssignedDbServer() {
        return assignedDbServer;
    }

    public void setAssignedDbServer(Server assignedDbServer) {
        this.assignedDbServer = assignedDbServer;
    }

    public void incrementNewDatabaseIndex(){
        newDatabaseIndex++;
    }

    public int getNewDatabaseIndex() {
        return newDatabaseIndex;
    }

    public void setNewDatabaseIndex(int newDatabaseIndex) {
        this.newDatabaseIndex = newDatabaseIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Server server = (Server) o;
        return port == server.port &&
                Objects.equals(ip, server.ip);
    }

    @Override
    public String toString() {
        return "ApplicationServer{" +
                "assignedDbServer=" + assignedDbServer +
                ", newDatabaseIndex=" + newDatabaseIndex +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
