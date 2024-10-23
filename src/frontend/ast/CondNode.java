package frontend.ast;

import frontend.token.TokenList;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code Cond -> LOrExp}
 */
public class CondNode extends ASTNode {
    private LOrExpNode cond;

    public CondNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        cond = new LOrExpNode(tokens, depth + 1);
        cond.parse();
    }

    public void analyzeSemantic(SymbolTable table) {
        cond.analyzeSemantic(table);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<Cond>\n").append(cond);
            return b.toString();
        }
        b.append(cond).append("<Cond>\n");
        return b.toString();
    }
}
