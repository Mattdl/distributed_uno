package stub_RMI.appserver_dbserver;

import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameDbStub extends Remote {

    boolean persistGame(Game game) throws RemoteException;
    boolean persistMove(String gameName, Move move) throws RemoteException;
    boolean persistPlayer(String gameName, Player player) throws RemoteException;

    Game fetchGame(String gameName) throws RemoteException;

}
