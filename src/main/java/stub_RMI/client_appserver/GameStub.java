package stub_RMI.client_appserver;

import model.Card;
import model.Game;
import model.Move;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameStub extends Remote {

    boolean hasEverybodyJoined(String gameName) throws RemoteException;

    //Get init game: Get own cards, Get number of other players cards
    Game getInitGameInfo() throws RemoteException;

    //Get played move
    Move getPlayedMove() throws RemoteException;

    //Play move






}
