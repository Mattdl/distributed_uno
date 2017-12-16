package stub_RMI.client_dispatcher;

import model.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherStub extends Remote {

    Server retrieveServerInfo() throws RemoteException; //return host-ip, port, serviceName

    Server retrieveActiveDatabaseInfo(Server currentAppServer) throws RemoteException;

    void clientQuitingSession(Server appServer) throws RemoteException;
}
