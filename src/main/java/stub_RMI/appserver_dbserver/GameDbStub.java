package stub_RMI.appserver_dbserver;

import model.Game;
import model.Move;
import model.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameDbStub extends Remote {

    boolean persistGame(Game game, boolean propagate) throws RemoteException;
    boolean persistMove(String gameId, Move move, boolean propagate) throws RemoteException;

    Game fetchGame(String gameId) throws RemoteException;

    void updateWinner(Player player) throws RemoteException;

    int fetchPlayerScore(String playerName) throws RemoteException;

}
