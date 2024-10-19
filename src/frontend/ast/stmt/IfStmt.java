package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.CondNode;
import frontend.ast.StmtNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * {@code IfStmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]}
 */
public class IfStmt extends ASTNode implements Statement {
    public enum Type {
        HasElse, NoElse
    }
    private Type type;
    private CondNode condition;
    private StmtNode ifStmt;
    private StmtNode elseStmt;

    public IfStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.IfKeyword, "if");
        expect(TokenType.LParenthesis, "(");
        condition = new CondNode(tokens, depth + 1);
        condition.parse();
        expect(TokenType.RParenthesis, ")");
        ifStmt = new StmtNode(tokens, depth + 1);
        ifStmt.parse();

        if (tokens.get().isTypeOf(TokenType.ElseKeyword)) {
            type = Type.HasElse;
            tokens.advance();
            elseStmt = new StmtNode(tokens, depth + 1);
            elseStmt.parse();
        } else {
            type = Type.NoElse;
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String s = "  ".repeat(depth);
            b.append(s).append("<IfStmt>\n").append(condition).append(ifStmt);
            if (type == Type.HasElse) {
                b.append(s).append("<ElseStmt>\n").append(elseStmt);
            }
            return b.toString();
        }
        b.append("IFTK if\n").append("LPARENT (\n").append(condition).append("RPARENT )\n").append(ifStmt);
        if (type == Type.HasElse) {
            b.append("ELSETK else\n").append(elseStmt);
        }
        return b.toString();
    }
}
