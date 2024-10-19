package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code ExpStmt -> [Exp] ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class ExpStmt extends ASTNode implements Statement {
    private ExpNode exp = null;

    public ExpStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        if (isExpr(tokens.get().getType())) {
            exp = new ExpNode(tokens, depth + 1);
            exp.parse();
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
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ExpStmt>\n");
            if (exp != null) {
                b.append(exp);
            }
            return b.toString();
        }
        if (exp != null) {
            b.append(exp);
        }
        b.append("SEMICN ;\n");
        return b.toString();
    }
}
