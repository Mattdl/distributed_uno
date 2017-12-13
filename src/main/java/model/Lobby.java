package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Lobby extends Observable implements Serializable {
    private List<Game> gameList;
    private int version;

    public Lobby(int version) {
        gameList = new ArrayList<Game>();
        this.version = version;
    }

    public Lobby(List<Game> games, int version) {
        gameList = games;
        this.version = version;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public Game findGame(String gameName) {

        int i = 0;

        while (i < gameList.size()) {
            if (gameList.get(i).getGameId().equals(gameName)) {
                return gameList.get(i);
            }
            i++;
        }
        return null;
    }

    /**
     * Adds a new game to the lobby's list of games
     *
     * @param game
     */
    public void addGame(Game game) {
        gameList.add(game);

        setChanged();
        notifyObservers();
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
        setChanged();
        notifyObservers();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;

        setChanged();
        notifyObservers();
    }

    public void updateVersion() {
        this.version++;

        setChanged();
        notifyObservers();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VERSION = " + version + "; GAMES = ");
        for (Game g : gameList) {
            sb.append(g + ", ");
        }
        return sb.toString();
    }

    public void copyLoby(Lobby retLobby) {
        this.gameList = retLobby.gameList;
        this.version = retLobby.version;

        setChanged();
        notifyObservers();
    }


}
