package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.CondNode;
import frontend.ast.ForStmtNode;
import frontend.ast.StmtNode;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code ForStmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt}
 */
public class ForStmt extends ASTNode implements Statement {
    /// {@code N} - condition or statement in this place is omitted</br>
    /// {@code Y} - condition or statement here exists</br>
    /// e.g. {@code NNY} - ForStmt1: Omitted, Cond: Omitted, ForStmt2: Exist
    public enum Type {
        NNN, NNY, NYN, NYY, YNN, YNY, YYN, YYY
    }
    private Type type;
    private ForStmtNode stmt1;
    private CondNode cond;
    private ForStmtNode stmt2;
    private StmtNode body;

    public ForStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        boolean isStmt1Exist = false, isStmt2Exist = false, isCondExist = false;
        expect(TokenType.ForKeyword, "for");
        expect(TokenType.LParenthesis, "(");
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.Semicolon)) {
            isStmt1Exist = true;
            stmt1 = new ForStmtNode(tokens, depth + 1);
            stmt1.parse();
        }

        expect(TokenType.Semicolon, ";");

        token = tokens.get();
        if (!token.isTypeOf(TokenType.Semicolon)) {
            isCondExist = true;
            cond = new CondNode(tokens, depth + 1);
            cond.parse();
        }

        expect(TokenType.Semicolon, ";");

        token = tokens.get();
        if (!token.isTypeOf(TokenType.RParenthesis)) {
            isStmt2Exist = true;
            stmt2 = new ForStmtNode(tokens, depth + 1);
            stmt2.parse();
        }
        expect(TokenType.RParenthesis, ")");

        body = new StmtNode(tokens, depth + 1);
        body.parse();

        // binary-like types
        int index = (isStmt2Exist ? 1 : 0) | (isCondExist ? 1 : 0) << 1 | (isStmt1Exist ? 1 : 0) << 2;
        type = Type.values()[index];
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        // for loop cannot declare new variables
        if (stmt1 != null) {
            stmt1.analyzeSemantic(table);
        }
        if (stmt2 != null) {
            stmt2.analyzeSemantic(table);
        }
        if (cond != null) {
            cond.analyzeSemantic(table);
        }
        // mark enter loop
        table.enterLoop();
        body.analyzeSemantic(table);
        // mark outside loop
        table.exitLoop();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ForStmt>\n")
                    .append(stmt1 == null ? "" : stmt1)
                    .append(cond == null ? "" : cond)
                    .append(stmt2 == null ? "" : stmt2)
                    .append(body);
            return b.toString();
        }
        b.append("FORTK for\n").append("LPARENT (\n")
                .append(stmt1 == null ? "" : stmt1).append("SEMICN ;\n")
                .append(cond == null ? "" : cond).append("SEMICN ;\n")
                .append(stmt2 == null ? "" : stmt2).append("RPARENT )\n")
                .append(body);
        return b.toString();
    }
}
