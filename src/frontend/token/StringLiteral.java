package frontend.token;

import util.Debug;

public class StringLiteral extends Token {
    public StringLiteral(int line, int col, String content) {
        super(TokenType.StringLiteral, line, col, content);
    }

    public static String display(String value) {
        StringBuilder builder = new StringBuilder();
        value.chars().forEach(c -> {
            switch (c) {
                case (char) 7 -> builder.append("\\a");
                case '\b' -> builder.append("\\b");
                case '\t' -> builder.append("\\t");
                case '\n' -> builder.append("\\n");
                case (char) 11 -> builder.append("\\v");
                case '\f' -> builder.append("\\f");
                case '\"' -> builder.append("\\\"");
                case '\'' -> builder.append("\\'");
                case '\\' -> builder.append("\\\\");
                case '\0' -> builder.append("\\0");
                default -> builder.append((char) c);
            }
        });
        return builder.toString();
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return String.format("Token_%-25s <line: %3d, col: %3d> %s", type, lineNumber, columnNumber, "\"" + display(content) + "\"");
        }
        return type + " " + "\"" + display(content) + "\"";
    }
}
