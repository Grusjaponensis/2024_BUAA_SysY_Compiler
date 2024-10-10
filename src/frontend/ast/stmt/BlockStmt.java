package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.BlockNode;
import frontend.token.TokenList;
import util.Debug;

/**
 * {@code BlockStmt -> Block}
 */
public class BlockStmt extends ASTNode implements Statement {
    private BlockNode block;

    public BlockStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        block = new BlockNode(tokens, depth + 1);
        block.parse();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append(block);
            return b.toString();
        }
        b.append(block);
        return b.toString();
    }
}
