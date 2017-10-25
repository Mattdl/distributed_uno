package client.controller;

import client.Main;
import client.service.LoginService;
import client.service.PingService;
import client.service.ServerInitiatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Server;


import java.util.logging.Level;
import java.util.logging.Logger;

import static client.Main.appServer;

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
                    LOGGER.log(Level.WARNING, "Appserver="+appServer +", ip="+appServer.getIp()+", port="+appServer.getPort());
                    //connectionText.setText("Has serverinfo: " + appServer.getIp() + ":" + appServer.getPort());
                }
            } else {
                LOGGER.warning("AppServer info is null!");
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

        //TODO encryption of username and password before giving to loginservice!

        //Init background service for login
        LoginService loginService = new LoginService(usernameInput.getText(), passwordInput.getText());

        loginService.setOnSucceeded(e -> {
            LOGGER.log(Level.INFO, "Login attempt finished");

            boolean isSuccessful = (Boolean) e.getSource().getValue();

            //return msg if succesfull
            String msg;
            if (isSuccessful) {
                msg = "Successfully logged in with username TODO"; //TODO
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
        //TODO similar as login
    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO, "switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO, "switched To LobbyScene");
    }
}
