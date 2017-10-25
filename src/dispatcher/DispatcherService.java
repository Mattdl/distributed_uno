package dispatcher;

import stub_RMI.appserver_dbserver.DispatcherStub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//RMI handles multi-threading itsel: may be or may not be in multiple threads
public class DispatcherService extends UnicastRemoteObject implements DispatcherStub {

    public DispatcherService() throws RemoteException {
    }


    /**
     * Method called by every new client
     * @return
     */
    @Override
    public synchronized String[] retrieveServerInfo() {


        return new String[0];
    }

}
