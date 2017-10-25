package dispatcher;

public class DispatcherThread extends Thread {

    @Override
    public void run() {

        //For each new client, make new thread


        //Make new thread for booting server
        new ServerBootThread().start();
    }
}
