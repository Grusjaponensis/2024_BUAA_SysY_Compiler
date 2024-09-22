package frontend.token;

public class Identifier extends Token {
    public Identifier(int line, int col, String content) {
        super(TokenType.Identifier, line, col, content);
    }
}
