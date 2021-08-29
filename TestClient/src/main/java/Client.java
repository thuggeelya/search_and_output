import ivf6.Help;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

class ClientThread extends Thread {

    @Override
    public void run() {

        //init();

        //while (true) {

            try (Help help = new Help("127.0.0.1", 8000)) {

                // char q = (char) 34;
                String request = "бурени+ or землян+";

                System.out.println("Searching for:\n" + request + "\n");

                help.writeLine(request);

                String response = null;

                response = help.readLine();

                System.out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }
}

public class Client {

    public static void main(String[] args) {

        ClientThread clientThread = new ClientThread();
        clientThread.start();

    }
}