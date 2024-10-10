package frontend.ast;

import exception.CompileError;
import frontend.token.TokenList;
import frontend.token.TokenType;
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

    public void parse() throws CompileError {
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
        if (mulExpNodes.isEmpty()) {
            throw new RuntimeException("Fuck you bitch!!!" + mulExpNodes);
        }
        b.append(mulExpNodes.get(0)).append("<AddExp>\n");
        for (int i = 1; i < mulExpNodes.size(); i++) {
            b.append(operators.get(i) == TokenType.PlusOperator ? "PLUS +\n" : "MINU -\n")
                    .append(mulExpNodes.get(i)).append("<AddExp>\n");
        }
        return b.toString();
    }
}
