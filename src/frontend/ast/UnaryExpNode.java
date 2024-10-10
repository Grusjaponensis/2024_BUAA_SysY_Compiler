package frontend.ast;

import exception.CompileError;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp}
 */
public class UnaryExpNode extends ASTNode {
    public enum Type {
        Primary, FuncCall, Unary
    }

    private Type type;
    private PrimaryExpNode primaryExp;
    private Ident identifier;
    private FuncCallParamsNode funcCallParams;
    private UnaryOp unaryOp;
    private UnaryExpNode unaryExp;

    public UnaryExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() throws CompileError {
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.PlusOperator) ||
                token.isTypeOf(TokenType.MinusOperator) ||
                token.isTypeOf(TokenType.NotOperator)) {
            // UnaryOp UnaryExp
            type = Type.Unary;
            unaryOp = new UnaryOp(token.getType());
            tokens.advance();
            unaryExp = new UnaryExpNode(tokens, depth + 1);
            unaryExp.parse();
        } else if (token.isTypeOf(TokenType.Identifier) && tokens.get(1).isTypeOf(TokenType.LParenthesis)) {
            // FuncCall
            type = Type.FuncCall;
            identifier = new Ident(token.getContent());
            tokens.advance();
            expect(TokenType.LParenthesis, "(");
            if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
                funcCallParams = new FuncCallParamsNode(tokens, depth + 1);
                funcCallParams.parse();
            }
            if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
                throw new CompileError(
                        tokens.prev().getLineNumber(), ErrorType.MissRparent, "got: " + token.getType()
                );
            } else {
                tokens.advance();
            }
        } else {
            // PrimaryExp
            type = Type.Primary;
            primaryExp = new PrimaryExpNode(tokens, depth + 1);
            primaryExp.parse();
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<UnaryExp>\n");
            switch (type) {
                case Primary -> b.append(primaryExp);
                case FuncCall -> {
                    b.append("  ".repeat(depth + 1)).append(identifier).append("()");
                    if (funcCallParams != null) {
                        b.append("\n").append(funcCallParams);
                    }
                }
                case Unary -> b.append("  ".repeat(depth)).append(unaryOp).append(unaryExp);
            }
            return b.toString();
        }
        switch (type) {
            case Primary -> b.append(primaryExp);
            case FuncCall -> {
                b.append(identifier).append("LPARENT (\n");
                if (funcCallParams != null) {
                    b.append(funcCallParams);
                }
                b.append("RPARENT )\n");
            }
            case Unary -> b.append(unaryOp).append(unaryExp);
        }
        b.append("<UnaryExp>\n");
        return b.toString();
    }
}
