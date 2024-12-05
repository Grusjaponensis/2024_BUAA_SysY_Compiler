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

    private void merge(char current, ArrayList<String> result) {
        if (result.isEmpty() ||
                (result.get(result.size() - 1).equals("%d") || result.get(result.size() - 1).equals("%c"))
        ) {
            result.add(String.valueOf(current));
        } else {
            result.set(result.size() - 1, result.get(result.size() - 1) + current);
        }
    }

    /**
     * Separates the input string into a list of format specifiers and non-format characters.
     *
     * <p>
     * For example:
     * </p>
     *
     * <pre>{@code
     * String value = "Hello %d world %c\n";
     * }</pre>
     *
     * <p>
     * This method will separate the string into the following list:
     * </p>
     *
     * <pre>{@code
     * ["Hello ", "%d", " world ", "%c", "\n"]
     * }</pre>
     *
     */
    public ArrayList<String> separate() {
        ArrayList<String> result = new ArrayList<>();
        int n = value.length();
        for (int i = 0; i < n; i++) {
            char current = value.charAt(i);
            if (i + 1 >= n) {
                // guard that will not out of bounds
                merge(current, result);
                break;
            }

            if (current == '%' && (value.charAt(i + 1) == 'd' || value.charAt(i + 1) == 'c')) {
                result.add("%" + value.charAt(i + 1));
                // skip next format char
                i++;
            } else {
                merge(current, result);
            }
        }
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
