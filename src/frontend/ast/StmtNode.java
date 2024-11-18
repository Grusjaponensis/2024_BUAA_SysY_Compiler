package frontend.ast;

import frontend.ast.stmt.*;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code Stmt ->}</br>
 * {@code LVal '=' Exp ';'}</br>
 * {@code | [Exp] ';'}</br>
 * {@code | Block}</br>
 * {@code | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]}</br>
 * {@code | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt}</br>
 * {@code | 'break' ';'}</br>
 * {@code | 'continue' ';'}</br>
 * {@code | 'return' [Exp] ';'}</br>
 * {@code | LVal '=' 'getint''('')'';'}</br>
 * {@code | LVal '=' 'getchar''('')'';'}</br>
 * {@code | 'printf' '(' StringConst { ',' Exp } ')' ';' }</br>
 */
public class StmtNode extends ASTNode {
    private Statement stmt;

    public StmtNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        switch (token.getType()) {
            case Identifier -> {
                int index = tokens.getIndex();
                if (!token.isTypeOf(TokenType.Semicolon)) {
                    // Field is not empty
                    // Since Exp consists of LVal, so first parse Exp
                    ExpNode exp = new ExpNode(tokens, depth + 1);
                    exp.parse();
                    if (tokens.get().isTypeOf(TokenType.AssignOperator)) {
                        // LVal assign statement
                        tokens.setIndex(index);
                        stmt = new LValAssignStmt(tokens, depth + 1);
                        stmt.parse();
                    } else {
                        // not null exp statement
                        tokens.setIndex(index);
                        stmt = new ExpStmt(tokens, depth + 1);
                        stmt.parse();
                    }
                } else {
                    stmt = new ExpStmt(tokens, depth + 1);
                }
            }
            case LBrace -> {
                // Block
                stmt = new BlockStmt(tokens, depth + 1);
                stmt.parse();
            }
            case IfKeyword -> {
                stmt = new IfStmt(tokens, depth + 1);
                stmt.parse();
            }
            case ForKeyword -> {
                stmt = new ForStmt(tokens, depth + 1);
                stmt.parse();
            }
            case BreakKeyword -> {
                stmt = new BreakStmt(tokens, depth + 1);
                stmt.parse();
            }
            case ContinueKeyword -> {
                stmt = new ContinueStmt(tokens, depth + 1);
                stmt.parse();
            }
            case ReturnKeyword -> {
                stmt = new ReturnStmt(tokens, depth + 1);
                stmt.parse();
            }
            case PrintfKeyword -> {
                stmt = new PrintfStmt(tokens, depth + 1);
                stmt.parse();
            }
            default -> {
                stmt = new ExpStmt(tokens, depth + 1);
                stmt.parse();
            }
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        stmt.analyzeSemantic(table);
    }

    public void generateIR(SymbolTable table) {
        stmt.generateIR(table);
    }

    public Statement getStmt() { return stmt; }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<Stmt>\n").append(stmt);
            return b.toString();
        }
        b.append(stmt).append("<Stmt>\n");
        return b.toString();
    }
}
