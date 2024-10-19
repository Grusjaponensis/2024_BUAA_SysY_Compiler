package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.Func;
import symbol.SymbolTable;
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

    public void parse() {
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
            if (isExpr(tokens.get().getType())) {
                funcCallParams = new FuncCallParamsNode(tokens, depth + 1);
                funcCallParams.parse();
            }
            // if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
            //     errors.add(new CompileError(
            //             tokens.prev().getLineNumber(), ErrorType.MissRparent, "got: " + token.getType()
            //     ));
            // } else {
            //     tokens.advance();
            // }
            expect(TokenType.RParenthesis, ")");
        } else {
            // PrimaryExp
            type = Type.Primary;
            primaryExp = new PrimaryExpNode(tokens, depth + 1);
            primaryExp.parse();
        }
    }

    private boolean isExpr(TokenType type) {
        return type == TokenType.PlusOperator ||
                type == TokenType.MinusOperator ||
                type == TokenType.NotOperator ||
                type == TokenType.LParenthesis ||
                type == TokenType.Identifier ||
                type == TokenType.IntLiteral ||
                type == TokenType.CharLiteral;
    }

    public void analyzeSemantic(SymbolTable table) {
        switch (type) {
            case Primary -> primaryExp.analyzeSemantic(table);
            case FuncCall -> {
                if (!table.hasSymbol(identifier.name())) {
                    // using undeclared identifier
                    // TODO: error handling
                }
                if (funcCallParams == null) {
                    Func func = (Func) table.find(identifier.name());
                    if (func.getParamsNum() != 0) {
                        // call parameterized function without parameters
                        // TODO: error handling: wrong number of parameters
                    }
                    return;
                }
                funcCallParams.analyzeSemantic(table, identifier.name());
            }
            case Unary -> unaryExp.analyzeSemantic(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
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
