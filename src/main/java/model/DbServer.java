package model;

import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.util.LinkedList;
import java.util.List;

public class DbServer extends Server {
    private GameDbStub gameDbStubs;
    private UserDbStub userDbStubs;

    public DbServer(String ip, int port) {
        super(ip, port);
    }

    public DbServer(String ip, int port, GameDbStub gameDbStubs, UserDbStub userDbStubs) {
        super(ip, port);
        this.gameDbStubs = gameDbStubs;
        this.userDbStubs = userDbStubs;
    }

    public GameDbStub getGameDbStubs() {
        return gameDbStubs;
    }

    public void setGameDbStubs(GameDbStub gameDbStubs) {
        this.gameDbStubs = gameDbStubs;
    }

    public UserDbStub getUserDbStubs() {
        return userDbStubs;
    }

    public void setUserDbStubs(UserDbStub userDbStubs) {
        this.userDbStubs = userDbStubs;
    }

    public boolean isConnected(){
        return gameDbStubs != null && userDbStubs != null;
    }
}
