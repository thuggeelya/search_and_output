import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBaseClass {

    protected boolean updateDB;
    protected String db;
    protected HashMap<String, Integer> amountOfUsages;

    public DataBaseClass(boolean updateDB) {
        this.updateDB = updateDB;
    }

    public int getUsagesOf(String company) {
        return this.amountOfUsages.get(company);
    }

    private void incUsagesOf(String company) {
        if(this.amountOfUsages.containsKey(company))
            this.amountOfUsages.replace(company, this.amountOfUsages.get(company) + 1);
        else
            this.amountOfUsages.put(company, 1);
    }

    public void setUpdateDB(boolean updateDB) {
        this.updateDB = updateDB;
    }

    public boolean isUpdateDB() {
        return updateDB;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getDb() {
        return db;
    }

    private @NotNull String readText() throws IOException {

        String fileName = "src/main/resources/response.json";

        File file = new File(fileName);

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    private @NotNull JSONArray companies(String jsonText) {

        JSONArray companies = new JSONArray();

        JSONObject jsonRoot = new JSONObject(jsonText);
        JSONArray jsonArray = jsonRoot.getJSONArray("items");
        for(int i=0; i < jsonArray.length(); i++) {
            JSONObject jsonEntities = jsonArray.getJSONObject(i);
            JSONObject jsonObjectI = jsonEntities.getJSONObject("ЮЛ");

            companies.put(jsonObjectI);
        }

        return companies;
    }

    public HashMap<String, String> findMatches(@NotNull ArrayList<String> mainCondition, @Nullable ArrayList<String> additionalCondition) throws IOException, URISyntaxException {

        HashMap<String, String> results = new HashMap<>();

        String jsonText;

        if(this.isUpdateDB()) {
            this.setDb(readText());
            this.setUpdateDB(false);
        }

        jsonText = this.getDb();

        JSONArray companies = companies(jsonText);

        HashSet<String> subs = new HashSet<>();

        for(String condition : mainCondition) {
            Pattern pattern = Pattern.compile(condition, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(jsonText);

            while (matcher.find()) {
                String sub = jsonText.substring(matcher.start(), matcher.end());
                subs.add(sub);
            }
        }

        if (additionalCondition != null) {
            for(String condition : additionalCondition) {
                Pattern pattern = Pattern.compile(condition, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(jsonText);

                while (matcher.find()) {
                    String sub = jsonText.substring(matcher.start(), matcher.end());
                    subs.add(sub);
                }
            }
        }

        String[] subsArray = subs.toArray(new String[0]);

        char q = (char) 34;

        boolean objContainsSubGlobal = false;

        String companyName = "НаимСокрЮЛ";

        for (Object o : companies) {

            if(objContainsSubGlobal) {
                this.incUsagesOf(companyName);
            }

            JSONObject jsonObject = (JSONObject) o;

            boolean objContainsSub;

            StringBuilder result = new StringBuilder();

            for (String key : jsonObject.keySet()) {
                String obj = jsonObject.get(key).toString();

                if(key.equals(companyName)) {
                    companyName = obj;
                    if(!this.amountOfUsages.containsKey(companyName))
                        this.amountOfUsages.put(companyName, 0);
                }

                for(String sub : subsArray) {

                    objContainsSub = obj.toLowerCase().contains(sub.toLowerCase());

                    if(objContainsSub) {
                        objContainsSubGlobal = true;
                        result.append(q).append(sub).append(q).append(" -> ").append(
                                                     obj.replace("{", "")
                                                        .replace("}", "")
                                                        .replace(String.valueOf(q), "")
                                                        .replace(":", ": ")
                                                        .replace("Текст", " описание")
                                                                ).append("\n");

                        if(results.containsKey(companyName))
                            results.replace(companyName, results.get(companyName) + result);
                        else
                            results.put(companyName, result.toString());
                    }
                    else {
                        objContainsSubGlobal = false;
                    }

                }

            }

        }

        if(objContainsSubGlobal) {
            this.incUsagesOf(companyName);
        }

        return (results.size() == 0) ? null : results;
    }

}