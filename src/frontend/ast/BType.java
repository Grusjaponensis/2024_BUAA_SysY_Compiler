package frontend.ast;

import frontend.token.TokenType;
import util.Debug;

/**
 * Terminating Symbol
 * </br>
 * {@code BType -> 'int' | 'char'}
 */
public record BType(TokenType type) {
    public BType {
        if (!(type == TokenType.CharKeyword || type == TokenType.IntKeyword)) {
            throw new RuntimeException("Invalid BType: " + type);
        }
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return type == TokenType.CharKeyword ? "char" : "int";
        }
        return type == TokenType.CharKeyword ? "CHARTK char\n" : "INTTK int\n";
    }
}
