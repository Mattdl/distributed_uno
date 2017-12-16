package model;

import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.util.LinkedList;
import java.util.List;

public class DbServer extends Server {
    private GameDbStub gameDbStub;
    private UserDbStub userDbStub;
    private boolean isOnline;

    // Used by Dispatcher
    private List<Server> assignedAppServers;

    public DbServer(String ip, int port) {
        super(ip, port);
        assignedAppServers = new LinkedList<>();
        isOnline = false;
    }

    public DbServer(String ip, int port, GameDbStub gameDbStub, UserDbStub userDbStub) {
        super(ip, port);
        this.gameDbStub = gameDbStub;
        this.userDbStub = userDbStub;
        assignedAppServers = new LinkedList<>();
        isOnline = false;
    }

    public GameDbStub getGameDbStub() {
        return gameDbStub;
    }

    public void setGameDbStub(GameDbStub gameDbStub) {
        this.gameDbStub = gameDbStub;
    }

    public UserDbStub getUserDbStub() {
        return userDbStub;
    }

    public void setUserDbStub(UserDbStub userDbStub) {
        this.userDbStub = userDbStub;
    }

    public boolean isConnected() {
        return gameDbStub != null && userDbStub != null;
    }

    public List<Server> getAssignedAppServers() {
        return assignedAppServers;
    }

    public void setAssignedAppServers(List<Server> assignedAppServers) {
        this.assignedAppServers = assignedAppServers;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "DbServer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    public String toDisplayString() {
        String base = getBaseDisplayString();

        if (isOnline) {
            return base + " = ONLINE";
        } else {
            return base + " = OFFLINE";
        }
    }

    public String getBaseDisplayString(){
        return "DB SERVER '" + ip + "':" + port;
    }
}
