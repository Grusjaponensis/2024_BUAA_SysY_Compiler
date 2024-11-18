package frontend.ast;

import frontend.token.StringLiteral;
import util.Debug;

import java.util.stream.IntStream;

public record StringConst(String value) {
    public int paramsNum() {
        return (int) IntStream.range(0, value.length() - 1)
                .filter(i -> value.charAt(i) == '%' &&
                        (value.charAt(i + 1) == 'd' || value.charAt(i + 1) == 'c'))
                .count();
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return "<StringConst> " + "\"" + StringLiteral.display(value) + "\"";
        }
        return "STRCON \"" + StringLiteral.display(value) + "\"\n";
    }
}
