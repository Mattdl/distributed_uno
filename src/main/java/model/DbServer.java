package model;

import stub_RMI.appserver_dbserver.GameDbStub;
import stub_RMI.appserver_dbserver.UserDbStub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DbServer extends Server {
    private GameDbStub gameDbStub;
    private UserDbStub userDbStub;
    private int assignedAppServerCount;
    private boolean isOnline;

    private List<Game> gameUpdateQueue = new ArrayList<>();
    private List<Move> moveUpdateQueue = new ArrayList<>();
    private List<User> userUpdateQueue = new ArrayList<>();


    public DbServer(String ip, int port) {
        super(ip, port);
        assignedAppServerCount = 0;
    }

    public DbServer(String ip, int port, GameDbStub gameDbStub, UserDbStub userDbStub) {
        super(ip, port);
        this.gameDbStub = gameDbStub;
        this.userDbStub = userDbStub;
        assignedAppServerCount = 0;
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

    public int getAssignedAppServerCount() {
        return assignedAppServerCount;
    }

    public void setAssignedAppServerCount(int assignedAppServerCount) {
        this.assignedAppServerCount = assignedAppServerCount;
    }

    public void incrementAssignedAppServerCount() {
        assignedAppServerCount++;
    }

    public void decrementAssignedAppServerCount() {
        assignedAppServerCount--;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<Game> getGameUpdateQueue() {
        return gameUpdateQueue;
    }

    public void addGameToQueue(Game game){
        gameUpdateQueue.add(game);
    }

    public void setGameUpdateQueue(List<Game> gameUpdateQueue) {
        this.gameUpdateQueue = gameUpdateQueue;
    }

    public List<Move> getMoveUpdateQueue() {
        return moveUpdateQueue;
    }

    public void addMoveToQueue(Move move){
        moveUpdateQueue.add(move);
    }

    public void setMoveUpdateQueue(List<Move> moveUpdateQueue) {
        this.moveUpdateQueue = moveUpdateQueue;
    }

    public List<User> getUserUpdateQueue() {
        return userUpdateQueue;
    }

    public void addUserToQueue(User user){
        userUpdateQueue.add(user);
    }

    public void setUserUpdateQueue(List<User> userUpdateQueue) {
        this.userUpdateQueue = userUpdateQueue;
    }

    @Override
    public String toString() {
        return "DbServer{" +
                "assignedAppServerCount=" + assignedAppServerCount +
                ", ip='" + ip + '\'' +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return port == server.port &&
                Objects.equals(ip, server.ip);
    }
}
