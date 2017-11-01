package client.service.lobby;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class JoinGameService extends Service<String> {

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                //TODO RMI call to join game

                return null;
            }
        };
    }
}
