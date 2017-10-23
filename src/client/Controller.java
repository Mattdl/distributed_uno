package client;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer{

    private Model model = new Model();

    //@FXML
    //private LineChart graph1;

    @Override
    public void update(Observable o, Object arg) {

    }
}
