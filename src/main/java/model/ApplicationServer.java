package model;

import java.util.LinkedList;
import java.util.List;

public class ApplicationServer extends Server {
    // Used by Dispatcher
    private List<Server> assignedDbServers;

    public ApplicationServer(String ip, int port) {
        super(ip, port);
        this.assignedDbServers = new LinkedList<>();
    }

    public List<Server> getAssignedDbServers() {
        return assignedDbServers;
    }

    public void setAssignedDbServers(List<Server> assignedDbServers) {
        this.assignedDbServers = assignedDbServers;
    }
}
