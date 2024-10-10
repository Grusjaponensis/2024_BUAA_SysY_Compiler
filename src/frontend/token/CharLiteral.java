package frontend.token;

import util.Debug;

public class CharLiteral extends Token {
    private final char value;

    public CharLiteral(int line, int column, String content) {
        super(TokenType.CharLiteral, line, column, content);
        // this.value = content.charAt(0);
        if (content.length() == 1) {
            value = content.charAt(0);
        } else {
            switch (content.charAt(1)) {
                case '\'' -> value = '\'';
                case '\"' -> value = '\"';
                case '\\' -> value = '\\';
                case 'a' -> value = (char) 7;
                case 'b' -> value = '\b';
                case 'f' -> value = '\f';
                case 'n' -> value = '\n';
                case 't' -> value = '\t';
                case '0' -> value = '\0';
                case 'v' -> value = (char) 11;
                default -> value = content.charAt(1);
            }
        }
    }

    public char getValue() { return value; }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return String.format("Token_%-25s <line: %3d, col: %3d> '%s", type, lineNumber, columnNumber, content + "'");
        }
        return type + " " + "'" + content + "'";
    }
}
