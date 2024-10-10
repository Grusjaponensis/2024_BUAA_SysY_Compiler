package frontend.ast;

import exception.CompileError;
import exception.ErrorType;
import frontend.token.*;
import util.Debug;

/**
 * {@code PrimaryExp -> '(' Exp ')' | LVal | Number | Character}
 */
public class PrimaryExpNode extends ASTNode {
    public enum Type {
        Exp, LVal, Num, Char
    }

    private ExpNode expNode;
    private LValNode lValNode;
    private Number number;
    private Char character;
    private Type type;

    public PrimaryExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() throws CompileError{
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.LParenthesis)) {
            type = Type.Exp;
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            expNode.parse();
            if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
                throw new CompileError(
                        tokens.prev().getLineNumber(), ErrorType.MissRparent, "got: " + token.getType()
                );
            } else {
                tokens.advance();
            }
        } else if (token.isTypeOf(TokenType.Identifier)) {
            type = Type.LVal;
            lValNode = new LValNode(tokens, depth + 1);
            lValNode.parse();
        } else if (token.isTypeOf(TokenType.IntLiteral)) {
            type = Type.Num;
            number = new Number(((IntegerLiteral) token).getValue());
            tokens.advance();
        } else {
            type = Type.Char;
            character = new Char(((CharLiteral) token).getValue());
            tokens.advance();
        }
    }

    public Type getType() { return type; }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<PrimaryExp>\n");
            switch (type) {
                case Exp -> b.append(expNode);
                case LVal -> b.append(lValNode);
                case Num -> b.append("  ".repeat(depth + 1)).append(number);
                case Char -> b.append("  ".repeat(depth + 1)).append(character);
            }
            return b.toString();
        }
        switch (type) {
            case Exp -> b.append("LPARENT (\n").append(expNode).append("RPARENT )\n");
            case LVal -> b.append(lValNode);
            case Num -> b.append(number);
            case Char -> b.append(character);
        }
        b.append("<PrimaryExp>\n");
        return b.toString();
    }
}
