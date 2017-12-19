package stub_RMI.client_appserver;

import model.Card;
import model.Game;
import model.Lobby;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyStub extends Remote {

    Lobby getJoinableGames(int version, String token) throws RemoteException;

    String createNewGame(Player initPlayer, String gameName, int gameSize, String token) throws RemoteException;

    Game getGameLobbyInfo(int version, String gameName, String token) throws RemoteException;

    String joinGame(Player player, String gameName, String token) throws RemoteException;

    String leaveGame(Player player, String gameName, String token) throws RemoteException;

}
