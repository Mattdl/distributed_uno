package client;

import javafx.fxml.FXML;

import java.awt.Button;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer{

    //private Model model = new Model();

    /* LOGIN SCREEN */
    @FXML
    private Button loginButton;

    @Override
    public void update(Observable o, Object arg) {

    }

    /* LOGIN SCREEN */
    @FXML
    public void tryLogin(){
        //TODO
    }

    @FXML
    public void tryRegister(){
        //TODO
    }
}
