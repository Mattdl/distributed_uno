package client.service.game_lobby;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service that obtains the information for an in-game Lobby
 */
public class GameLobbyService extends Service<Void> {

    private boolean inGameLobby;

    public GameLobbyService() {
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
