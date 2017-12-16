package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ApplicationServer extends Server {
    // Used by Dispatcher
    private DbServer assignedDbServer;
    private int newDatabaseIndex;
    private int assignedClientsCount;

    public ApplicationServer() {
        this.assignedDbServer = null;
        newDatabaseIndex = 0;
        assignedClientsCount = 0;
    }

    public ApplicationServer(String ip, int port) {
        super(ip, port);
        this.assignedDbServer = null;
        newDatabaseIndex = 0;
        assignedClientsCount = 0;
    }

    public ApplicationServer(String ip, int port, DbServer assignedDbServer) {
        super(ip, port);
        this.assignedDbServer = assignedDbServer;
        newDatabaseIndex = 0;
        assignedClientsCount = 0;
    }

    public DbServer getAssignedDbServer() {
        return assignedDbServer;
    }

    public void setAssignedDbServer(DbServer assignedDbServer) {
        this.assignedDbServer = assignedDbServer;
    }

    public void incrementNewDatabaseIndex() {
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

    public int getAssignedClientsCount() {
        return assignedClientsCount;
    }

    public void setAssignedClientsCount(int assignedClientsCount) {
        this.assignedClientsCount = assignedClientsCount;
    }

    public void incrementClientCount() {
        assignedClientsCount++;
    }

    public void decrementClientCount() {
        assignedClientsCount--;
    }

    @Override
    public String toString() {
        return "ApplicationServer{" +
                "assignedDbServer=" + assignedDbServer +
                ", newDatabaseIndex=" + newDatabaseIndex +
                ", assignedClientsCount=" + assignedClientsCount +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
