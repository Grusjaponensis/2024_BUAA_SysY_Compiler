package frontend.ast;

import frontend.token.StringLiteral;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.IntStream;

public record StringConst(String value) {
    public int paramsNum() {
        return (int) IntStream.range(0, value.length() - 1)
                .filter(i -> value.charAt(i) == '%' &&
                        (value.charAt(i + 1) == 'd' || value.charAt(i + 1) == 'c'))
                .count();
    }

    public ArrayList<String> separate() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '%' && i + 1 >= value.length()) {
                // guard that will not out of bounds
                if (result.isEmpty()) {
                    result.add("%");
                } else {
                    result.set(result.size() - 1, result.get(result.size() - 1) + value.charAt(i));
                }
                break;
            }

            if (!(value.charAt(i) == '%' && (value.charAt(i + 1) == 'd' || value.charAt(i + 1) == 'c'))) {
                // not format param, can be combined into a new string
                if (result.isEmpty()) {
                    result.add(String.valueOf(value.charAt(i)));
                } else {
                    String original = result.get(result.size() - 1);
                    result.set(result.size() - 1, original + value.charAt(i));
                }
            } else {
                if (!result.isEmpty() && !result.get(result.size() - 1).isEmpty()) {
                    result.add("");
                }
                i++;
            }
        }
        result.removeIf(String::isEmpty);
        return result;
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return "<StringConst> " + "\"" + StringLiteral.display(value) + "\"";
        }
        return "STRCON \"" + StringLiteral.display(value) + "\"\n";
    }
}
