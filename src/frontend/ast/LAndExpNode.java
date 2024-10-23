package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code LAndExp -> EqExp | LAndExp '&&' EqExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code LAndExp -> { EqExp '&&' } EqExp}
 * </pre>
 */
public class LAndExpNode extends ASTNode {
    private final ArrayList<EqExpNode> eqExpNodes = new ArrayList<>();

    public LAndExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        EqExpNode eqExpNode = new EqExpNode(tokens, depth + 1);
        eqExpNode.parse();
        eqExpNodes.add(eqExpNode);

        while (tokens.get().isTypeOf(TokenType.AndOperator)) {
            tokens.advance();
            eqExpNode = new EqExpNode(tokens, depth + 1);
            eqExpNode.parse();
            eqExpNodes.add(eqExpNode);
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        eqExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<LAndExp>\n");
            eqExpNodes.forEach(b::append);
            return b.toString();
        }
        b.append(eqExpNodes.get(0)).append("<LAndExp>\n");
        for (int i = 1; i < eqExpNodes.size(); i++) {
            b.append("AND &&\n").append(eqExpNodes.get(i)).append("<LAndExp>\n");
        }
        return b.toString();
    }
}
