package stub_RMI.appserver_dbserver;

import model.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameDbStub extends Remote {

    boolean persistGame(Game game, boolean propagate) throws RemoteException;

    boolean persistMove(String gameId, Move move, boolean propagate) throws RemoteException;

    Game fetchGame(String gameId) throws RemoteException;

    void updateWinner(Player player) throws RemoteException;

    int fetchPlayerScore(String playerName) throws RemoteException;

    List<Game> fetchQueueingGameUpdates(Server requestingDbServer) throws RemoteException;

    List<Game> fetchQueueingMoveUpdates(Server requestingDbServer) throws RemoteException;

    List<Card> fetchCardImageMappings(boolean isSpecialEdition) throws RemoteException;
}
