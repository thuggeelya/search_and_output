import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AnalyzeInput {

    private final Operator operator;

    public ArrayList<String> words; // and / or / nD / nW

    public boolean isAnd = false;
    public boolean isOr = false;
    public boolean isND = false;
    public boolean isNW = false;

    public String inputtedOperator;
    public int n;

    public AnalyzeInput(@NotNull Operator operator) {
        this.operator = operator;
        findOperator();
    }

    private void findOperator() {
        this.words = null;
        this.words = operator.findOperator(".+(?=\\s(and))", "(?<=and\\s).+");
        this.words = (this.words == null) ? operator.findOperator(".+(?=\\s(or))", "(?<=or\\s).+") : this.words;
        this.words = (this.words == null) ? operator.findOperator(".+(?=\\s(\\d+?d))", "(?<=\\d+?d\\s).+") : this.words;
        this.words = (this.words == null) ? operator.findOperator(".+(?=\\s(\\d+?w))", "(?<=\\d+?w\\s).+") : this.words;
        this.words = (this.words == null) ? operator.findOperator(null, "[\\S\\s]+") : this.words;

        this.n = 0;

        if((this.words != null) && (this.words.size() > 1))
            defineOperator();
    }

    private void defineOperator() {
        this.inputtedOperator = this.words.get(1);

        if (this.inputtedOperator.equals("and")) {
            this.isAnd = true;
            this.words.remove(1);
        }

        if (this.inputtedOperator.equals("or")) {
            this.isOr = true;
            this.words.remove(1);
        }

        String reg = "(\\d+)d";
        if (this.inputtedOperator.matches(reg)) {
            this.isND = true;
            defineN();
            this.words.remove(1);
        }

        reg = "(\\d+)w";
        if (this.inputtedOperator.matches(reg)) {
            this.isNW = true;
            defineN();
            this.words.remove(1);
        }
    }

    private void defineN() {
        this.n = Integer.parseInt(this.inputtedOperator.substring(0, this.inputtedOperator.length()-1));
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public boolean isAnd() {
        return isAnd;
    }

    public boolean isOr() {
        return isOr;
    }

    public boolean isND() {
        return isND;
    }

    public int getN() {
        return n;
    }
}