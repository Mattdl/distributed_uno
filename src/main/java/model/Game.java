package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

//@DatabaseTable(tableName = "game")
public class Game extends Observable implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    //@DatabaseField(generatedId = true)
    //private int id;

    //@DatabaseField
    private State state;

    //@DatabaseField
    private boolean clockwise;

    //@DatabaseField(foreign = true)
    private List<Player> playerList;

    //@DatabaseField(foreign = true)
    private List<Card> deck;

    //@DatabaseField(foreign = true)
    private List<Move> moves;

    //@DatabaseField
    private String gameName;

    //@DatabaseField
    private int gameSize;

    private int joinedPlayers;

    private int version;

    //private String password;

    public Game(String gameName, int gameSize, Player initialPlayer) {
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        this.version = 0;
        playerList.add(initialPlayer);
    }

    public Game(String gameName, int gameSize, Player initialPlayer, int version) {
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        this.version = version;
        playerList.add(initialPlayer);
    }

    /**
     * Adds a player to the game
     *
     * @param player
     */
    public synchronized void addPlayer(Player player) {
        playerList.add(player);
        setChanged();
        notifyObservers();
    }

    public boolean isJoinable() {
        return playerList.size() < gameSize;
    }

    public boolean isStartable() {
        return playerList.size() == gameSize;
    }

    public synchronized boolean removePlayer(Player player) {
        LOGGER.log(Level.INFO,"Removing player from game");

        int i = 0;

        while ( i < playerList.size()) {
            //LOGGER.log(Level.INFO,"In the while");

            if(playerList.get(i).equals(player)){
                playerList.remove(i);
                LOGGER.log(Level.INFO, "Removed player: {0}", player);

                setChanged();
                notifyObservers();
                return true;
            }
            i++;
        }

        LOGGER.log(Level.INFO, "Did not find player = {0}", player);

        return false;
    }

    public void addJoinedPlayer() {
        joinedPlayers++;

        setChanged();
        notifyObservers();
    }

    public void makeCopy(Game serverSideGame) {
        this.gameName = serverSideGame.gameName;
        this.gameSize = serverSideGame.gameSize;
        this.playerList = serverSideGame.playerList;
        this.clockwise = serverSideGame.clockwise;
        this.state = serverSideGame.state;
        this.version = serverSideGame.version;

        setChanged();
        notifyObservers();
    }

    public void updateVersion() {
        this.version++;
    }

    public enum State {
        WAITING,
        COUNTING,
        RUNNING
    }

    //GETTERS & SETTERS
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(int joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Game{" +
                "state=" + state +
                ", clockwise=" + clockwise +
                ", playerList=" + playerList +
                ", deck=" + deck +
                ", moves=" + moves +
                ", gameName='" + gameName + '\'' +
                ", gameSize=" + gameSize +
                ", joinedPlayers=" + joinedPlayers +
                '}';
    }
}

