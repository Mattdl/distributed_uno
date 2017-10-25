package client.controller;

import client.Main;
import client.Model;
import client.service.LoginService;
import client.service.PingService;
import client.service.ServerInitiatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Server;

import java.awt.TextField;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.Main.appServer;

public class LoginController{

    private static final Logger LOGGER = Logger.getLogger( LoginController.class.getName() );

    private boolean hasServerConnection = false;

    @FXML
    private Text connectionTextField;


    @FXML
    public void initialize(){
        ServerInitiatorService serverInitiatorService = new ServerInitiatorService();
        serverInitiatorService.setOnSucceeded(event -> {

            Server serverInfo = (Server) event.getSource().getValue();

            if(serverInfo != null){
                appServer = serverInfo;
                connectionTextField.setText("Has serverinfo: " + serverInfo.getIp() + ":" + serverInfo.getPort());
            }
            else{
                LOGGER.warning("AppServer info is null!");
            }

            //Check if retrieved info has valid connection
            PingService pingService = new PingService();
            pingService.setOnSucceeded(event1 -> {
                boolean isConnected = (boolean) event1.getSource().getValue();
                if(isConnected) {
                    connectionTextField.setText("Connection established: " + serverInfo.getIp() + ":" + serverInfo.getPort());
                }else{
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
        LOGGER.log(Level.INFO,"Trying Login");

        //Init background service for login
        LoginService loginService = new LoginService();

        loginService.setOnSucceeded( e -> {
            LOGGER.log(Level.INFO,"Login attempt finished");

            boolean isSuccessful = (Boolean) e.getSource().getValue();

            //return msg if succesfull
            String msg;
            if(isSuccessful) {
                msg = "Successfully logged in with username TODO"; //TODO
                LOGGER.log(Level.INFO,"Login attempt was SUCCESSFUL");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchToLobbyScene(stage,msg);
            }
            else{
                msg= "Could not login, try again.";
                LOGGER.log(Level.WARNING,"Login attempt FAILED");
            }
        });
        loginService.start();
    }

    @FXML
    public void tryRegister() {
        //TODO similar as login
    }

    private void switchToLobbyScene(Stage stage, String msg) {
        LOGGER.log(Level.INFO,"switching To LobbyScene");

        stage.setScene(Main.sceneFactory.getLobbyScene(msg));

        LOGGER.log(Level.INFO,"switched To LobbyScene");
    }
}
