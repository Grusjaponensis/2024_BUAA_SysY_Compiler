package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code ContinueStmt -> 'continue' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class ContinueStmt extends ASTNode implements Statement {
    public ContinueStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.ContinueKeyword, "continue");
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return "  ".repeat(depth) + "<ContinueStmt> continue\n";
        }
        return "CONTINUETK continue\nSEMICN ;\n";
    }
}
