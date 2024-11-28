package frontend.ast;

import frontend.token.TokenList;
import ir.IRValue;
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

    public int evaluate(SymbolTable table) {
        return addExp.evaluate(table);
    }

    public IRValue generateIR(SymbolTable table) {
        return addExp.generateIR(table);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<Exp>\n").append(addExp);
            return b.toString();
        }
        b.append(addExp).append("<Exp>\n");
        return b.toString();
    }
}
