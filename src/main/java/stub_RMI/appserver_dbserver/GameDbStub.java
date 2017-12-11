package stub_RMI.appserver_dbserver;

import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameDbStub extends Remote {

    boolean persistGame(Game game) throws RemoteException;
    boolean persistMove(Game game, Move move) throws RemoteException;
    boolean persistPlayer(Game game, Player player) throws RemoteException;

}
