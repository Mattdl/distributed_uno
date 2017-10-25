package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private State state;
    private boolean clockwise;
    private List<Player> playerList;
    private List<Card> deck;
    private String gameName;
    private int gameSize;
    //private Password

    public Game(String gameName, int gameSize, Player initialPlayer){
        this.gameName = gameName;
        this.gameSize = gameSize;
        this.playerList = new ArrayList<>();
        this.clockwise = true;
        this.state = State.WAITING;
        playerList.add(initialPlayer);
    }

    /**
     * Adds a player to the game
     * @param player
     */
    public void addPlayer(Player player){
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

    public List<Player> getPlayerList() {
        return playerList;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public String getGameName() {
        return gameName;
    }

    public int getGameSize() {
        return gameSize;
    }

    public enum State{
        WAITING,
        COUNTING,
        RUNNING
    }

}

