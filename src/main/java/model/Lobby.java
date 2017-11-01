package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Lobby extends Observable {
    private List<Game> gameList;

    public Lobby() {
        gameList = new ArrayList<Game>();
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public int findGameIndex(Game game) {
        boolean found = false;
        int i = 0;

        while (!found && i < gameList.size()) {
            if (gameList.get(i).getGameName().equals(game.getGameName())) {
                found = true;
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Adds a new game to the lobby's list of games
     *
     * @param game
     */
    public void addGame(Game game) {
        gameList.add(game);
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
        setChanged();
        notifyObservers();
    }
}
