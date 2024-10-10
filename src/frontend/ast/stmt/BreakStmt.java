package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code BreakStmt -> 'break' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class BreakStmt extends ASTNode implements Statement {
    public BreakStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.BreakKeyword, "break");
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return "  ".repeat(depth) + "<BreakStmt> break\n";
        }
        return "BREAKTK break\nSEMICN ;\n";
    }
}
