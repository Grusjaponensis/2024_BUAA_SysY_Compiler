package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBasicBlock;
import ir.IRBuilder;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code LOrExp -> LAndExp | LOrExp '||' LAndExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code LOrExp -> { LAndExp '||' } LAndExp}
 * </pre>
 */
public class LOrExpNode extends ASTNode {
    private final ArrayList<LAndExpNode> andExpNodes = new ArrayList<>();

    public LOrExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        LAndExpNode andExpNode = new LAndExpNode(tokens, depth + 1);
        andExpNode.parse();
        andExpNodes.add(andExpNode);

        while (tokens.get().isTypeOf(TokenType.OrOperator)) {
            tokens.advance();
            andExpNode = new LAndExpNode(tokens, depth + 1);
            andExpNode.parse();
            andExpNodes.add(andExpNode);
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        andExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    public void generateIR(SymbolTable table, IRBasicBlock destTrue, IRBasicBlock destFalse) {
        for (int i = 0; i < andExpNodes.size() - 1; i++) {
            // first generate BB for next andExp
            IRBasicBlock destNext = new IRBasicBlock(IRBuilder.getInstance().blockReg());
            // if any of these andExp is true, branch to true bb
            andExpNodes.get(i).generateIR(table, destTrue, destNext);
            // set BB for the next andExp IR
            IRBuilder.getInstance().addBasicBlock(destNext);
        }
        andExpNodes.get(andExpNodes.size() - 1).generateIR(table, destTrue, destFalse);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<LOrExp>\n");
            andExpNodes.forEach(b::append);
            return b.toString();
        }
        b.append(andExpNodes.get(0)).append("<LOrExp>\n");
        for (int i = 1; i < andExpNodes.size(); i++) {
            b.append("OR ||\n").append(andExpNodes.get(i)).append("<LOrExp>\n");
        }
        return b.toString();
    }
}
