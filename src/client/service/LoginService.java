package client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<Boolean> {
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                boolean isSuccessful=true;

                //TODO RMI call

                return isSuccessful; //Calls succeeded()
            }
        };
    }
}
