package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DatabaseTable(tableName = "game")
public class Game {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private State state;

    @DatabaseField
    private boolean clockwise;

    @DatabaseField(foreign = true)
    private Collection<Player> playerList;

    @DatabaseField(foreign = true)
    private Collection<Card> deck;

    @DatabaseField(foreign = true)
    private Collection<Move> moves;

    @DatabaseField
    private String gameName;

    @DatabaseField
    private int gameSize;
    //private Password

    public Game(String gameName, int gameSize, Player initialPlayer) {
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        playerList.add(initialPlayer);
    }

    /**
     * Adds a player to the game
     *
     * @param player
     */
    public void addPlayer(Player player) {
        playerList.add(player);
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(Collection<Player> playerList) {
        this.playerList = playerList;
    }

    public Collection<Card> getDeck() {
        return deck;
    }

    public void setDeck(Collection<Card> deck) {
        this.deck = deck;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public String getGameName() {
        return gameName;
    }

    public int getGameSize() {
        return gameSize;
    }

    public boolean isJoinable() {
        return playerList.size() < gameSize;
    }

    public enum State {
        WAITING,
        COUNTING,
        RUNNING
    }

}

