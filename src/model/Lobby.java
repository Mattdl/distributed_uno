package model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
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

}
