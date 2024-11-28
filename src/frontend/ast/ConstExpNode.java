package frontend.ast;

import frontend.token.TokenList;
import symbol.SymbolTable;
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
        addExp.parse();
    }

    public void analyzeSemantic(SymbolTable table) {
        addExp.analyzeSemantic(table);
    }

    public int evaluate(SymbolTable table) {
        return addExp.evaluate(table);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<ConstExp>\n").append(addExp);
            return b.toString();
        }
        b.append(addExp).append("<ConstExp>\n");
        return b.toString();
    }
}
