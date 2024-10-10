package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code ForStmt -> LVal '=' Exp}
 */
public class ForStmtNode extends ASTNode {
    private LValNode lVal;
    private ExpNode exp;

    public ForStmtNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        lVal = new LValNode(tokens, depth + 1);
        try {
            lVal.parse();
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }

        expect(TokenType.AssignOperator, "=");

        exp = new ExpNode(tokens, depth + 1);
        try {
            exp.parse();
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ForStmt>\n").append(lVal).append("\n").append(exp);
            return b.toString();
        }
        b.append(lVal).append("ASSIGN =\n").append(exp).append("<ForStmt>\n");
        return b.toString();
    }
}
