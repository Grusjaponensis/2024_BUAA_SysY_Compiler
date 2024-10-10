package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code LVal -> Ident [ '[' Exp ']' ]}
 */
public class LValNode extends ASTNode {
    private Ident identifier;
    private ExpNode expNode;

    public LValNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() throws CompileError {
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.Identifier)) {
            throw new RuntimeException("Identifier expected, got: " + token.getType());
        }
        identifier = new Ident(token.getContent());
        tokens.advance();
        if (tokens.get().isTypeOf(TokenType.LBracket)) {
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            try {
                expNode.parse();
            } catch (CompileError e) {
                ErrorCollector.getInstance().addError(e);
            }
            if (!tokens.get().isTypeOf(TokenType.RBracket)) {
                throw new CompileError(
                       tokens.prev().getLineNumber(), ErrorType.MissRbrack, "got: " + token.getType()
                );
            } else {
                tokens.advance();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<LVal> ").append(identifier).append("\n");
            if (expNode != null) {
                b.append(expNode);
            }
            return b.toString();
        }
        b.append(identifier);
        if (expNode != null) {
            b.append("LBRACK [\n").append(expNode).append("RBRACK ]\n");
        }
        b.append("<LVal>\n");
        return b.toString();
    }
}
