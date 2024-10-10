package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import frontend.token.TokenList;
import util.Debug;

/**
 * {@code ConstExp -> AddExp}
 */
public class ConstExpNode extends ASTNode {
    private AddExpNode addExp;

    public ConstExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        addExp = new AddExpNode(tokens, depth + 1);
        try {
            addExp.parse();
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space);
            b.append("<ConstExp>\n");
            b.append(addExp);
            return b.toString();
        }
        b.append(addExp);
        b.append("<ConstExp>\n");
        return b.toString();
    }
}
