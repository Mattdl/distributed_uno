package stub_RMI.client_appserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameLobbyStub extends Remote {
    boolean hasEverybodyJoined(String gameName, String token) throws RemoteException;
}
