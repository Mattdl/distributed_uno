package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@DatabaseTable(tableName = "game")
public class Game {

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

    //private String password;

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

    public boolean isJoinable() {
        return playerList.size() < gameSize;
    }

    public void removePlayer(Player player) {
        int i=0;

        while(!playerList.get(i).equals(player) && i < playerList.size()){
            i++;
        }
        if(i< playerList.size()){
            playerList.remove(i);
        }
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
}

