package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code Block -> '{' { BlockItem } '}'}
 */
public class BlockNode extends ASTNode {
    private final ArrayList<BlockItemNode> blockItems = new ArrayList<>();

    public BlockNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        expect(TokenType.LBrace, "{");
        while (!tokens.get().isTypeOf(TokenType.RBrace)) {
            BlockItemNode node = new BlockItemNode(tokens, depth + 1);
            node.parse();
            blockItems.add(node);
        }
        tokens.advance();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<Block>\n");
            blockItems.forEach(b::append);
            return b.toString();
        }
        b.append("LBRACE {\n");
        blockItems.forEach(b::append);
        b.append("RBRACE }\n").append("<Block>\n");
        return b.toString();
    }
}
