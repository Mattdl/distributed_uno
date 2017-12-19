package client.service.lobby;

import client.Main;
import dispatcher.Dispatcher;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import model.Game;
import model.Lobby;
import stub_RMI.client_appserver.LobbyStub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gets list of games for the client
 */
public class LobbyService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger( LobbyService.class.getName() );

    private Lobby lobby;
    private boolean isInLobby;

    public LobbyService(Lobby lobby) {
        this.lobby = lobby;
        this.isInLobby = true;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Registry myRegistry = LocateRegistry.getRegistry(Main.appServer.getIp(), Main.appServer.getPort());

                LobbyStub lobbyService = (LobbyStub) myRegistry.lookup("LobbyService");
                int version = -1;

                while(isInLobby){
                    Lobby retLobby = lobbyService.getJoinableGames(version, Main.token);

                    version = retLobby.getVersion();

                    LOGGER.log(Level.INFO, "Received gamelist: {0}", retLobby);

                    if(isInLobby) {
                        LOGGER.log(Level.INFO, "UPDATING LOBBY INFO: {0}", retLobby);
                        lobby.copyLoby(retLobby);
                    }
                }

                return null;
            }
        };
    }

    public void setInLobby(boolean inLobby) {
        isInLobby = inLobby;
    }
}
