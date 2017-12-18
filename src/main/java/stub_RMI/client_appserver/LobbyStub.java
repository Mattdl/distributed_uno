package stub_RMI.client_appserver;

import model.Card;
import model.Game;
import model.Lobby;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyStub extends Remote {

    Lobby getJoinableGames(int version) throws RemoteException;

    String createNewGame(Player initPlayer, String gameName, int gameSize, String password) throws RemoteException;

    Game getGameLobbyInfo(int version, String gameName) throws RemoteException;

    String joinGame(Player player, String gameName) throws RemoteException;

    String leaveGame(Player player, String gameName) throws RemoteException;

}
