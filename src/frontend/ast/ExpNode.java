package frontend.ast;

import frontend.token.TokenList;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code Exp -> AddExp}
 */
public class ExpNode extends ASTNode {
    private AddExpNode addExp;

    public ExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        addExp = new AddExpNode(tokens, depth + 1);
        addExp.parse();
    }

    public void analyzeSemantic(SymbolTable table) {
        addExp.analyzeSemantic(table);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<Exp>\n").append(addExp);
            return b.toString();
        }
        b.append(addExp);
        b.append("<Exp>\n");
        return b.toString();
    }
}
