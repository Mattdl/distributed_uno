package client.controller;

import client.Main;
import client.service.login.LoginService;
import client.service.login.PingService;
import client.service.login.RegisterService;
import client.service.login.ServerInitiatorService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Player;
import model.Server;
import stub_RMI.client_dispatcher.DispatcherStub;
import sun.security.util.Password;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.Main.appServer;
import static client.service.login.ServerInitiatorService.DISPATCHER_IP;
import static client.service.login.ServerInitiatorService.DISPATCHER_PORT;
import static client.service.login.ServerInitiatorService.DISPATCHER_SERVICE;

public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    private PasswordField passwordInput;

    @FXML
    private TextField usernameInput;

    @FXML
    private Text connectionText;


    @FXML
    public void initialize() {

        //TODO check if token is valid, if token is valid => go to Lobby
        ServerInitiatorService serverInitiatorService = new ServerInitiatorService();
        serverInitiatorService.setOnSucceeded(event -> {

            Server serverInfo = (Server) event.getSource().getValue();

            if (serverInfo != null) {
                appServer = serverInfo;
                if (appServer.getIp() != null && appServer.getPort() != -1) {
                    LOGGER.log(Level.INFO, "Appserver=" + appServer + ", ip=" + appServer.getIp() + ", port=" + appServer.getPort());

                    Stage stage = (Stage) usernameInput.getScene().getWindow();
                    updateStageToSentNotifyWhenLeaving(stage);

                    //connectionText.setText("Has serverinfo: " + appServer.getIp() + ":" + appServer.getPort());
                }
            } else {
                LOGGER.warning("ApplicationServer info is null!");
            }

            //Check if retrieved info has valid connection
            PingService pingService = new PingService();
            pingService.setOnSucceeded(event1 -> {
                boolean isConnected = (boolean) event1.getSource().getValue();
                if (isConnected) {
                    connectionText.setText("Connection established: " + serverInfo.getIp() + ":" + serverInfo.getPort());


                } else {
                    LOGGER.warning("Could not connect to the retrieved server from dispatch!");
                }
            });
            pingService.start();
        });
        serverInitiatorService.start();
    }

    /* LOGIN SCREEN */
    @FXML
    public void tryLogin(ActionEvent event) {
        LOGGER.log(Level.INFO, "Trying Login");

        String username = usernameInput.getText();

        //TODO encryption of username and password before giving to loginservice!

        //Init background service for login
        LoginService loginService = new LoginService(username, passwordInput.getText());

        loginService.setOnSucceeded(e -> {
            LOGGER.log(Level.INFO, "Login attempt finished");

            boolean isSuccessful = (Boolean) e.getSource().getValue();

            //return msg if succesfull
            String msg;
            if (isSuccessful) {
                Main.currentPlayer = new Player(username);
                msg = "Successfully logged in as " + username; //TODO
                LOGGER.log(Level.INFO, "Login attempt was SUCCESSFUL");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToLobbyScene(stage, msg);
            } else {
                msg = "Could not login, try again.";
                LOGGER.log(Level.WARNING, "Login attempt FAILED");
            }
        });
        loginService.start();
    }

    @FXML
    public void tryRegister() {
        LOGGER.log(Level.INFO, "Trying register");

        //Init background service for login
        RegisterService registerService = new RegisterService(usernameInput.getText(), passwordInput.getText());

        registerService.setOnSucceeded(e -> {
            LOGGER.log(Level.INFO, "register attempt finished");

            boolean isSuccessful = (Boolean) e.getSource().getValue();

            //return msg if succesfull
            String msg;
            if (isSuccessful) {
                connectionText.setText("Registered successful");
            } else {
                connectionText.setText("Something went wrong with the registration");
                LOGGER.log(Level.WARNING, "register attempt FAILED");
            }
        });
        registerService.start();
    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }

    /**
     * Only needs to be set when client has connection to an app server.
     *
     * @param stage
     */
    private void updateStageToSentNotifyWhenLeaving(Stage stage) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                //RMI init
                Registry myRegistry = LocateRegistry.getRegistry(DISPATCHER_IP, DISPATCHER_PORT);
                DispatcherStub dispatcherService = (DispatcherStub) myRegistry.lookup(DISPATCHER_SERVICE);

                dispatcherService.clientQuitingSession(Main.appServer);

                return null;
            }
        };

        task.setOnSucceeded(e -> {
                    Platform.exit();
                }

        );

        stage.setOnHidden(event -> {
            task.run();
        });
    }
}
