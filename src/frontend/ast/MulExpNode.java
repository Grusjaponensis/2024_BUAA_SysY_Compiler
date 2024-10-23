package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code MulExp -> { UnaryExp ('*' | '/' | '%') } UnaryExp}
 * </pre>
 */
public class MulExpNode extends ASTNode {
    private final ArrayList<UnaryExpNode> unaryExpNodes = new ArrayList<>();
    private final ArrayList<TokenType> operators = new ArrayList<>();

    public MulExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
        // the first term does not have an operator
        operators.add(null);
    }

    public void parse() {
        UnaryExpNode unaryExpNode = new UnaryExpNode(tokens, depth + 1);
        unaryExpNodes.add(unaryExpNode);
        unaryExpNode.parse();
        while (tokens.get().isTypeOf(TokenType.MultiplyOperator) ||
                tokens.get().isTypeOf(TokenType.DivideOperator) ||
                tokens.get().isTypeOf(TokenType.ModOperator)) {
            operators.add(tokens.get().getType());
            tokens.advance();
            unaryExpNode = new UnaryExpNode(tokens, depth + 1);
            unaryExpNodes.add(unaryExpNode);
            unaryExpNode.parse();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        unaryExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<MulExp>\n").append(unaryExpNodes.get(0));
            for (int i = 1; i < unaryExpNodes.size(); i++) {
                b.append("\n").append(space).append(operators.get(i)).append("\n").append(unaryExpNodes.get(i));
            }
            b.append("\n");
            return b.toString();
        }
        b.append(unaryExpNodes.get(0));
        b.append("<MulExp>\n");
        for (int i = 1; i < unaryExpNodes.size(); i++) {
            b.append(operators.get(i) == TokenType.MultiplyOperator ?
                    "MULT *\n" : operators.get(i) == TokenType.DivideOperator ?
                    "DIV /\n" : "MOD %\n"
            );
            b.append(unaryExpNodes.get(i));
            b.append("<MulExp>\n");
        }
        return b.toString();
    }
}
