package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code RelExp -> { AddExp ('<' | '>' | '<=' | '>=') } AddExp}
 * </pre>
 */
public class RelExpNode extends ASTNode {
    private final ArrayList<AddExpNode> addExpNodes = new ArrayList<>();
    private final ArrayList<TokenType> operators = new ArrayList<>();

    public RelExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
        operators.add(null);
    }

    public void parse() {
        AddExpNode addExpNode = new AddExpNode(tokens, depth + 1);
        addExpNode.parse();
        addExpNodes.add(addExpNode);

        while (tokens.get().isTypeOf(TokenType.LTOperator) ||
                tokens.get().isTypeOf(TokenType.GTOperator) ||
                tokens.get().isTypeOf(TokenType.LEOperator) ||
                tokens.get().isTypeOf(TokenType.GEOperator)) {
            operators.add(tokens.get().getType());
            tokens.advance();
            addExpNode = new AddExpNode(tokens, depth + 1);
            addExpNode.parse();
            addExpNodes.add(addExpNode);
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        addExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<RelExp>\n").append(addExpNodes.get(0));
            for (int i = 1; i < addExpNodes.size(); i++) {
                b.append(space).append(operators.get(i)).append("\n").append(addExpNodes.get(i));
            }
            return b.toString();
        }
        b.append(addExpNodes.get(0)).append("<RelExp>\n");
        for (int i = 1; i < addExpNodes.size(); i++) {
            switch (operators.get(i)) {
                case LTOperator -> b.append("LSS <\n");
                case GTOperator -> b.append("GRE >\n");
                case LEOperator -> b.append("LEQ <=\n");
                case GEOperator -> b.append("GEQ >=\n");
            }
            b.append(addExpNodes.get(i)).append("<RelExp>\n");
        }
        return b.toString();
    }
}
