package client.service.game_lobby;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Game;

/**
 * Service that obtains the information for an in-game Lobby
 */
public class GameLobbyService extends Service<Void> {

    private Game game;
    private boolean inGameLobby;

    public GameLobbyService(Game game) {
        this.game = game;
        inGameLobby = true;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while(inGameLobby){
                    //TODO RMI call for Game-data
                }
                return null;
            }
        };
    }
}
