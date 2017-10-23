package client.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<String> {
    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                //TODO RMI call

                //return msg if succesfull
                String msg = "Successfully logged in with username TODO"; //TODO
                return msg;
            }
        };
    }
}
