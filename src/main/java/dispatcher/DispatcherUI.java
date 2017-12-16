package dispatcher;

import model.DbServer;

import javax.swing.*;
import java.awt.*;

public class DispatcherUI extends Dispatcher {
    private JFrame frame;

    public DispatcherUI() {
    }

    private void start() {
        init();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("DISPATCHER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Control panel for the dispatcher.");
        frame.getContentPane().add(label);

        for (DbServer dbServer : dbServers) {
            Button button = new Button(dbServer.toDisplayString());
            button.addActionListener((e) -> {

                DbServer updatedDbServer = doDatabaseServerAction(dbServer);

                button.setLabel(updatedDbServer.toDisplayString());

                updateUI();
            });

            frame.getContentPane().add(button);
        }

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void updateUI() {
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    private DbServer doDatabaseServerAction(DbServer dbServer) {
        dbServer = findDbServerFromString(dbServer.getBaseDisplayString());

        dbServer.setOnline(!dbServer.isOnline());

        //TODO actually shut down the database, simulate crash
        //dbServer.shutDown();

        return dbServer;

    }

    private DbServer findDbServerFromString(String baseString) {

        for (DbServer dbServer : dbServers) {
            if (dbServer.getBaseDisplayString().equals(baseString)) {
                return dbServer;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new DispatcherUI().start();
    }
}
