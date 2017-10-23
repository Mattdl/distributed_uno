package client;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FetchService extends Service<Void> {
    private boolean online;

    public FetchService() {
        this.online = true;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                //The fetching loop
                while(online){

                    //TODO RMI call to fetch Game-data

                    //TODO update Game-model
                }

                return null;
            }
        };
    }
}
