package stub_RMI.client_appserver;

import model.Game;
import model.Lobby;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyStub extends Remote {

    Lobby getJoinableGames(int version) throws RemoteException;

    String createNewGame(Player initPlayer, String gameName, int gameSize) throws RemoteException;

    Game getGameLobbyInfo(int version, String gameName) throws RemoteException;

    String joinGame(Player player, String gameName) throws RemoteException;

    String leaveGame(Player player, String gameName) throws RemoteException;
}
