package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.ASTNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code BreakStmt -> 'break' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class BreakStmt extends ASTNode implements Statement {
    private int lineNum;

    public BreakStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.BreakKeyword, "break");
        lineNum = tokens.prev().getLineNumber();
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        if (!table.isInLoop()) {
            ErrorCollector.getInstance().addError(
                    new CompileError(lineNum, ErrorType.NonLoopStmt)
            );
        }
    }

    @Override
    public void generateIR(SymbolTable table) {

    }

    @Override
    public String toString() {
        addErrors();
        if (Debug.DEBUG_STATE) {
            return "  ".repeat(depth) + "<BreakStmt> break\n";
        }
        return "BREAKTK break\nSEMICN ;\n";
    }
}
