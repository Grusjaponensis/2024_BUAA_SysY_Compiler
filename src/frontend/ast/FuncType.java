package frontend.ast;

import frontend.token.TokenType;
import symbol.ValueType;
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

    public ValueType valueType() {
        switch (type) {
            case IntKeyword -> { return ValueType.Int; }
            case CharKeyword -> { return ValueType.Char; }
            default -> { return ValueType.Void; }
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
