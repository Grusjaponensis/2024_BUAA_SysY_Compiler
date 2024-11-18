package frontend.ast;

import frontend.ast.stmt.Statement;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;

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

    public void analyzeSemantic(SymbolTable table) {
        if (isDecl) {
            decl.analyzeSemantic(table);
        } else {
            stmt.analyzeSemantic(table);
        }
    }

    public void generateIR(SymbolTable table) {
        if (isDecl) {
            decl.generateIR(table, false);
        } else {
            stmt.generateIR(table);
        }
    }

    public Statement getStmt() { return stmt.getStmt(); }

    public boolean isDecl() { return isDecl; }

    @Override
    public String toString() {
        return isDecl ? decl.toString() : stmt.toString();
    }
}
