package frontend;

import frontend.ast.CompUnit;
import frontend.token.TokenList;

public class Parser {
    private final TokenList tokens;

    public Parser(TokenList tokens) {
        this.tokens = tokens;
    }

    public CompUnit parse() {
        CompUnit compUnit = new CompUnit(tokens, 0);
        compUnit.parse();
        return compUnit;
    }
}
