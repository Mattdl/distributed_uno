package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Lobby extends Observable {
    private List<Game> gameList;

    public Lobby(){
        gameList = new ArrayList<Game>();
    }

    public List<Game> getGameList() {
        return gameList;
    }

    /**
     * Adds a new game to the lobby's list of games
     * @param game
     */
    public void addGame(Game game){
        gameList.add(game);
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
        setChanged();
        notifyObservers();
    }
}
