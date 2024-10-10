package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;

/**
 * {@code BlockItem -> Decl | Stmt}
 */
public class BlockItemNode extends ASTNode {
    private boolean isDecl;
    private DeclNode decl;
    private StmtNode stmt;

    public BlockItemNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.ConstKeyword) ||
                token.isTypeOf(TokenType.CharKeyword) ||
                token.isTypeOf(TokenType.IntKeyword)) {
            isDecl = true;
            decl = new DeclNode(tokens, depth + 1);
            decl.parse();
        } else {
            isDecl = false;
            stmt = new StmtNode(tokens, depth + 1);
            stmt.parse();
        }
    }

    @Override
    public String toString() {
        return isDecl ? decl.toString() : stmt.toString();
    }
}
