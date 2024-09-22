package frontend.token;

public class IntegerLiteral extends Token {
    private final int value;

    public IntegerLiteral(int line, int col, String content) {
        super(TokenType.IntLiteral, line, col, content);
        this.value = Integer.parseInt(content);
    }

    public int getValue() { return value; }
}
