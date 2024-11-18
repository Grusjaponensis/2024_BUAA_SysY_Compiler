package frontend.ast;

import frontend.token.TokenType;
import util.Debug;

public record UnaryOp(TokenType type) {
    public UnaryOp {
        if (type != TokenType.PlusOperator &&
                type != TokenType.MinusOperator &&
                type != TokenType.NotOperator) {
            throw new RuntimeException("Invalid unary operator: " + type);
        }
    }

    @Override
    public String toString() {
        String s = (type == TokenType.PlusOperator ? "+\n" : type == TokenType.MinusOperator ? "-\n" : "!\n");
        if (Debug.DEBUG_STATE) {
            return "<UnaryOp> " + s;
        }
        return type + " " + s + "<UnaryOp>\n";
    }
}
