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

    public DataBaseClass(boolean updateDB) {
        this.updateDB = updateDB;
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

    private @NotNull JSONArray companies(@NotNull String jsonText) {

        JSONArray companies = new JSONArray();

        int index = jsonText.indexOf("{");
        jsonText = jsonText.substring(index);

        JSONObject jsonRoot = new JSONObject(jsonText.trim());

        JSONArray jsonArray = jsonRoot.getJSONArray("items");

        for(int i=0; i < jsonArray.length(); i++) {
            JSONObject jsonEntities = jsonArray.getJSONObject(i);
            JSONObject jsonObjectI = jsonEntities.getJSONObject("ЮЛ");

            companies.put(jsonObjectI);
        }

        return companies;
    }

    public HashMap<Company, String> findMatches(@NotNull ArrayList<String> mainCondition, @Nullable ArrayList<String> additionalCondition) throws IOException, URISyntaxException {

        HashMap<Company, String> companyResult = new HashMap<>();

        String jsonText;

        if(this.isUpdateDB()) {
            this.setDb(readText());
            this.setUpdateDB(false);
        }

        jsonText = this.getDb();

        JSONArray companies = companies(jsonText);

        HashSet<String> subs = new HashSet<>(); // all subs
        HashSet<String> subsMain = new HashSet<>(); // necessary subs

        for(String condition : mainCondition) {
            Pattern pattern = Pattern.compile(condition, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(jsonText);

            while (matcher.find()) {
                String sub = jsonText.substring(matcher.start(), matcher.end());
                subs.add(sub);
                subsMain.add(sub);
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

        for (Object o : companies) {

            JSONObject jsonObject = (JSONObject) o;

            boolean objContainsSub = false;

            StringBuilder result = new StringBuilder();

            Company company = new Company();

            for (String key : jsonObject.keySet()) {
                String obj = jsonObject.get(key).toString();

                String value = obj.replace("{", "")
                        .replace("}", "")
                        .replace(String.valueOf(q), "")
                        .replace(":", ": ")
                        .replace("Текст", " описание");

                addCompany(company, key, value);

                for(String sub : subsMain.toArray(new String[0])) {

                    objContainsSub = obj.toLowerCase().contains(sub.toLowerCase());

                    if(!objContainsSub)
                        break;
                }

                if(objContainsSub)
                for(String sub : subsArray) {

                    objContainsSub = obj.toLowerCase().contains(sub.toLowerCase());

                    StringBuilder resTemp = new StringBuilder();

                    if(objContainsSub) {

                        resTemp.append(q).append(sub).append(q).append(" -> ").append(value).append("\n");
                        result.append(resTemp);

                        companyResult.put(company, result.toString());
                        company.incUsages();
                    }

                }

            }

        }

        return (companyResult.size() == 0) ? null : companyResult;
    }

    private void addCompany(Company company, @NotNull String key, String obj) {
        switch(key) {
            case "ИНН" -> company.setInn(obj);
            case "КПП" -> company.setKpp(obj);
            case "ОГРН" -> company.setOrgn(obj);
            case "ДатаОГРН" -> company.setDateOrgn(obj);
            case "Статус" -> company.setStatus(obj);
            case "Капитал" -> company.setCapital(obj);
            case "АА" -> company.setAa(obj);
            case "НаимПолнЮЛ" -> company.setName(obj);
            case "Адрес" -> company.setAddress(obj);
            case "Руководитель" -> company.setLeader(obj);
            case "ОснВидДеят" -> company.setDesc(obj);
        }
    }

}