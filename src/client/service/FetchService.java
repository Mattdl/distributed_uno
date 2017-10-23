package client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchService extends Service<Void> {

    private static final Logger LOGGER = Logger.getLogger( FetchService.class.getName() );

    private boolean online;

    public FetchService() {
        this.online = true;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                LOGGER.log(Level.INFO,"Calling background Task");

                //The fetching loop
                while(online){

                    //TODO RMI call to fetch Game-data

                    LOGGER.log(Level.INFO,"GameData fetch received: {0}");

                    //TODO update Game-model
                }

                return null;
            }
        };
    }
}
