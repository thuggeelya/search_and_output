import org.jetbrains.annotations.NotNull;
import ivf6.Help;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.*;
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

            ArrayList<Integer> companiesUsages = new ArrayList<>();

            while (go_on.get()) {

                Help help = new Help(server);
                String currentState = myServer.currentState(myServer); // (1) -> state search

                if(myServer.currentState(myServer).equals("")) { // (2) -> to search state

                    new Thread(() -> { //run()

                        System.out.println(currentState + "\n");

                        String request = null;

                        request = help.readLine();

                        String response = null;

                        if(!Objects.equals(request, "") && request != null) {
                            ArrayList<String> mainCondition = new ArrayList<>();
                            ArrayList<String> additionalCondition = new ArrayList<>();

                            Operator temp = new Operator(request);
                            AnalyzeInput analyzeInput = new AnalyzeInput(temp);

                            String[] input = temp.delQuotes(analyzeInput.getWords().toArray(new String[0]));
                            for (int i = 0; i < input.length; i++) {
                                input[i] = input[i].replaceAll("(?<=\\S)\\+", "[??-????-??a-zA-Z0-9\\-]*");
                            }

                            int n = analyzeInput.getN();

                            if (analyzeInput.isAnd()) {
                                mainCondition.add(input[0]);
                                mainCondition.add(input[1]);
                            } else if (analyzeInput.isOr()) {
                                mainCondition.add(input[0]);
                                additionalCondition.add(input[1]);
                            } else {
                                // nW
                                mainCondition.add((input.length > 1) ? input[0] + "(\\s\\S+){0," + n + "}\\s" + input[1] : input[0]);

                                if (analyzeInput.isND()) {
                                    additionalCondition.add(input[1] + "(\\s\\S+){0," + n + "}\\s" + input[0]);
                                }
                            }

                            HashMap<Company, String> results = null;

                            try {
                                results = db.findMatches(mainCondition, additionalCondition);
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }

                            StringBuilder matches = new StringBuilder();

                            if (results != null) {

                                companiesUsages.clear();

                                for (Company company : results.keySet()) {
                                    companiesUsages.add(company.getUsages());
                                }

                                companiesUsages.sort(Collections.reverseOrder());

                                for(int usage : companiesUsages)
                                    for(Company company : results.keySet())
                                        if (company.getUsages() == usage) {
                                            matches
                                                    .append(company.getAa())
                                                    .append("\n")
                                                    .append(results.get(company))
                                                    .append("\nAddress: ")
                                                    .append(company.getAddress())
                                                    .append("\n" + (char) 34 + "????????" + (char) 34 + ": ")
                                                    .append(company.getOrgn())
                                                    .append("\nCurrent status: ")
                                                    .append(company.getStatus()).append("\n\n");
                                            companiesUsages.set(companiesUsages.indexOf(usage), -1);
                                        }
/*
                                for (String key : results.keySet()) {
                                    matches.append(key).append("\n").append(results.get(key)).append("\n");
                                }

 */

                            }
                            else
                                matches.append("No matches ..");

                            System.out.println(matches);

                            response = matches.toString().replace("\n", "_");
                        }
                        //else {
                        //    response = "Error ..";
                        //}

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