package dispatcher;

import app_server.AppServer;
import db_server.DatabaseServer;
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
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        Container pane = frame.getContentPane();

        BorderLayout borderLayout = new BorderLayout();
        pane.setLayout(borderLayout);

        JPanel panelNorth = new JPanel();
        JPanel panelCenter = new JPanel();
        JPanel panelBottom = new JPanel();


        // NORTH
        JLabel label = new JLabel("Control panel for the dispatcher.");
        panelNorth.add(label);
        frame.add(panelNorth, BorderLayout.NORTH);

        LOGGER.info("DISPLAYING BUTTONS FOR dbservers = {}", dbServers);

        // CENTER
        panelCenter.add(new JLabel("Database Controls"));
        for (DbServer dbServer : dbServers) {
            JButton button = new JButton(dbServer.toDisplayString());
            button.addActionListener((e) -> {

                DbServer updatedDbServer = doDatabaseServerAction(dbServer);
                button.setText(updatedDbServer.toDisplayString());
                //button.updateUI();

                //updateUI()
            });

            panelCenter.add(button);
        }
        frame.add(panelCenter, BorderLayout.CENTER);

        // BOTTOM
        String base = "IS HOLIDAY = ";
        JButton button = new JButton(base + Dispatcher.isHolliday);
        button.addActionListener((e) -> {
            Dispatcher.isHolliday = !Dispatcher.isHolliday;
            button.setText(base + Dispatcher.isHolliday);
        });
        panelBottom.add(new JLabel("AppServer controls"));
        panelBottom.add(button);
        frame.add(panelBottom, BorderLayout.SOUTH);

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

        if (dbServer != null) {

            if (dbServer.isOnline()) {

                //TODO actually shut down the database, simulate crash
                DatabaseServer.stopDatabaseServer(dbServer);
                dbServer.setOnline(false);

            } else {
                startDbServer(dbServer);
                dbServer.setOnline(true);
            }

            // Update status
        }

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
