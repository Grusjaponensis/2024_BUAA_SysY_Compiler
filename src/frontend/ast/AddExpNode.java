package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code AddExp -> MulExp | AddExp ('+' | '−') MulExp}</br>
 * Rewrite the grammar to:
 * <pre>
 * {@code AddExp -> { MulExp ('+' | '-') } MulExp}
 * </pre>
 */
public class AddExpNode extends ASTNode {
    private final ArrayList<MulExpNode> mulExpNodes = new ArrayList<>();
    private final ArrayList<TokenType> operators = new ArrayList<>();

    public AddExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
        operators.add(TokenType.PlusOperator);
    }

    public void parse() {
        MulExpNode mulExpNode = new MulExpNode(tokens, depth + 1);
        mulExpNodes.add(mulExpNode);
        mulExpNode.parse();
        while (tokens.get().isTypeOf(TokenType.PlusOperator) || tokens.get().isTypeOf(TokenType.MinusOperator)) {
            operators.add(tokens.get().getType());
            tokens.advance();
            mulExpNode = new MulExpNode(tokens, depth + 1);
            mulExpNodes.add(mulExpNode);
            mulExpNode.parse();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        mulExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<AddExp>\n").append(mulExpNodes.get(0));
            for (int i = 1; i < mulExpNodes.size(); i++) {
                b.append(space).append(operators.get(i)).append("\n").append(mulExpNodes.get(i));
            }
            b.append("\n");
            return b.toString();
        }
        b.append(mulExpNodes.get(0)).append("<AddExp>\n");
        for (int i = 1; i < mulExpNodes.size(); i++) {
            b.append(operators.get(i) == TokenType.PlusOperator ? "PLUS +\n" : "MINU -\n")
                    .append(mulExpNodes.get(i)).append("<AddExp>\n");
        }
        return b.toString();
    }
}
