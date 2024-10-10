package frontend.ast;

import frontend.token.TokenType;
import util.Debug;

/**
 * {@code FuncType -> 'void' | 'int' | 'char'}
 */
public record FuncType(TokenType type) {
    public FuncType {
        if (!(type == TokenType.IntKeyword || type == TokenType.CharKeyword || type == TokenType.VoidKeyword)) {
            throw new RuntimeException("Invalid function type: " + type);
        }
    }

    @Override
    public String toString() {
        String s = type == TokenType.IntKeyword ? "int" : type == TokenType.CharKeyword ? "char" : "void";
        if (Debug.DEBUG_STATE) {
            return s;
        }
        return type.toString() + " " + s + "\n" + "<FuncType>\n";
    }
}
