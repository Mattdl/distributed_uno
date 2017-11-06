package app_server.service;

import model.Game;
import model.Lobby;
import stub_RMI.client_appserver.GameLobbyStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Service to check if everybody joined the game from the GameLobby
 */
public class GameLobbyService extends UnicastRemoteObject implements GameLobbyStub {


    private Lobby lobby;
    //private GameDbService gameDbService;

    public GameLobbyService(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    /**
     * RMI call that is called on client side after that a player has initialized his game.
     * It only returns true when all players in the game have joined (and thus initialized) the game.
     * @param gameName
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean hasEverybodyJoined(String gameName) throws RemoteException {
        try {
            Game game = lobby.findGame(gameName);
            game.addJoinedPlayer();
            if (game.getJoinedPlayers() < game.getPlayerList().size()) {
                wait();
            }
            else{
                notifyAll();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
