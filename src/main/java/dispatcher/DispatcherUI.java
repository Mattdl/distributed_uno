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
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        Container pane = frame.getContentPane();

        BorderLayout borderLayout = new BorderLayout();
        pane.setLayout(borderLayout);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Control panel for the dispatcher.");
        pane.add(label,BorderLayout.NORTH);

        LOGGER.info("DISPLAYING BUTTONS FOR dbservers = {}",dbServers);

        for (DbServer dbServer : dbServers) {
            JButton button = new JButton(dbServer.toDisplayString());
            button.addActionListener((e) -> {

                DbServer updatedDbServer = doDatabaseServerAction(dbServer);
                button.setText(updatedDbServer.toDisplayString());
                //button.updateUI();

                //updateUI()
                });

            pane.add(button,BorderLayout.CENTER);
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
