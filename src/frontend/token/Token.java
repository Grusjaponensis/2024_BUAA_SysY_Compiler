package frontend.token;

import util.Debug;

public abstract class Token {
    protected final TokenType type;
    protected final int lineNumber;
    protected final int columnNumber;
    protected final String content;

    public Token(TokenType type, int lineNumber, int columnNumber, String content) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.content = content;
        this.columnNumber = columnNumber;
    }

    public TokenType getType() { return type; }

    public int getLineNumber() { return lineNumber; }

    public String getContent() { return content; }

    public boolean isTypeOf(TokenType type) { return this.type == type; }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return String.format("Token_%-25s <line: %3d, col: %3d> %s", type, lineNumber, columnNumber, content);
        }
        return type + " " + content;
    }
}
