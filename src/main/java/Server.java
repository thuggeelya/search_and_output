import org.jetbrains.annotations.NotNull;
import ivf6.Help;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private State state;

    public Server(){
        this.state = new StateFinish(this);
    }

    public void changeState(State state) {
        this.state = state;
    }
    public State getState() {
        return state;
    }
    public String currentState(@NotNull Server myServer) {
        return myServer.getState().onSearch();
    }

    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(8000)) {

            AtomicBoolean go_on = new AtomicBoolean(true);
            Server myServer = new Server();

            DataBaseClass db = new DataBaseClass(true);

            while (go_on.get()) {

                Help help = new Help(server);
                String currentState = myServer.currentState(myServer); // (1) -> state search

                if(myServer.currentState(myServer).equals("")) { // (2) -> to search state

                    new Thread(() -> { //run()

                        System.out.println(currentState + "\n");
                        String request = help.readLine();

                        ArrayList<String> mainCondition = new ArrayList<>();
                        ArrayList<String> additionalCondition = new ArrayList<>();

                        Operator temp = new Operator(request);
                        AnalyzeInput analyzeInput = new AnalyzeInput(temp);

                        String[] input = temp.delQuotes(analyzeInput.getWords().toArray(new String[2]));
                        for(int i=0; i<input.length; i++) {
                            input[i] = input[i].replaceAll("(?<=\\S)\\+", "[а-яА-Яa-zA-Z0-9\\-]*");
                        }

                        int n = analyzeInput.getN();

                        if(analyzeInput.isAnd()) {
                            mainCondition.add(input[0]);
                            mainCondition.add(input[1]);
                        }
                        else
                        if(analyzeInput.isOr()) {
                            mainCondition.add(input[0]);
                            additionalCondition.add(input[1]);
                        }
                        else {
                            // nW
                            mainCondition.add(input[0] + "(\\s\\S+){0," + n + "}\\s" + input[1]);

                            if (analyzeInput.isND()) {
                                additionalCondition.add(input[1] + "(\\s\\S+){0," + n + "}\\s" + input[0]);
                            }
                        }

                        HashMap<String, String> results = null;

                        try {
                            results = db.findMatches(mainCondition, additionalCondition);
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }

                        StringBuilder matches = new StringBuilder();

                        if(results != null)
                            for(String key : results.keySet()) {
                                matches.append(key).append("\n").append(results.get(key)).append("\n");
                            }
                        else
                            matches.append("No matches ..");

                        System.out.println(matches);

                        String response = matches.toString().replace("\n", "_");

                        help.writeLine(response);

                        try {
                            help.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }).start();

                }
            }
        }
    }
}