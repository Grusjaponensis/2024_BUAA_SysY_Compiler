package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code FuncCallParams -> Exp { ',' Exp }}</br>
 * aka. {@code FuncRParams}
 */
public class FuncCallParamsNode extends ASTNode {
    private final ArrayList<ExpNode> params = new ArrayList<>();

    public FuncCallParamsNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        ExpNode expNode = new ExpNode(tokens, depth + 1);
        try {
            expNode.parse();
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }
        params.add(expNode);

        while (tokens.get().isTypeOf(TokenType.Comma)) {
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            try {
                expNode.parse();
            } catch (CompileError e) {
                ErrorCollector.getInstance().addError(e);
            }
            params.add(expNode);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space);
            b.append("<FuncCallParams>\n");
            params.forEach(b::append);
            b.append("\n");
            return b.toString();
        }
        b.append(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            b.append("COMMA ,\n").append(params.get(i));
        }
        b.append("<FuncRParams>\n");
        return b.toString();
    }
}
