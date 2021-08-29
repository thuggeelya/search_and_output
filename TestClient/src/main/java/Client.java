import ivf6.Help;

import java.io.IOException;
import java.util.Objects;

class ClientThread extends Thread {

    @Override
    public void run() {

        while (true) {
            try (Help help = new Help("127.0.0.1", 8000)) {

                char q = (char) 34;
                String request = "испытан+" + " or " + q + "Обработк+ металл+" + q;

                System.out.println("Searching for:\n" + request + "\n");

                help.writeLine(request);
                String response = help.readLine();

                if (!Objects.equals(response, null)) {

                    System.out.println(response);

                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Client {

    public static void main(String[] args) {
        ClientThread clientThread = new ClientThread();
        clientThread.start();
    }
}