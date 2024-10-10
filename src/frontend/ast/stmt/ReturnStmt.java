package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code ReturnStmt -> 'return' [Exp] ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class ReturnStmt extends ASTNode implements Statement {
    private ExpNode returnExp;

    public ReturnStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.ReturnKeyword, "return");
        if (isExpr(tokens.get().getType())) {
            returnExp = new ExpNode(tokens, depth + 1);
            try {
                returnExp.parse();
            } catch (CompileError e) {
                ErrorCollector.getInstance().addError(e);
            }
        }
        expect(TokenType.Semicolon, ";");
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ReturnStmt>\n");
            if (returnExp != null) {
                b.append(returnExp);
            }
            return b.toString();
        }
        b.append("RETURNTK return\n");
        if (returnExp != null) {
            b.append(returnExp);
        }
        b.append("SEMICN ;\n");
        return b.toString();
    }
}
