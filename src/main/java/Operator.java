import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Operator(String request) {

    public @Nullable ArrayList<String> findOperator(String reg1, String reg2) {

        ArrayList<String> matches = new ArrayList<>();

        String requestTemp = this.request;

        if(reg1 != null) {
            Pattern pattern = Pattern.compile(reg1, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(requestTemp);
            while (matcher.find()) {
                String sub = requestTemp.substring(matcher.start(), matcher.end());
                matches.add(sub);
                requestTemp = requestTemp.substring(sub.length());
                matches.add(matcher.group(1));
            }

            if (matches.size() == 0) {
                return null;
            }
        }

        Pattern pattern2 = Pattern.compile(reg2, Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(requestTemp);
        while (matcher2.find()) {
            matches.add(requestTemp.substring(matcher2.start(), matcher2.end()));
        }

        return matches;
    }

    @Contract("_ -> param1")
    public String @NotNull [] delQuotes(String @NotNull [] input) {
        int len = input.length;
        char q = (char) 34;
        for(int i=0; i<len; i++) {
            input[i] = input[i].replace(String.valueOf(q), "");
        }

        return input;
    }

}