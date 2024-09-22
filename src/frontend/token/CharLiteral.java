package frontend.token;

import util.Debug;

public class CharLiteral extends Token {
    private final char value;

    public CharLiteral(int line, int column, String content) {
        super(TokenType.CharLiteral, line, column, content);
        this.value = content.charAt(0);
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
