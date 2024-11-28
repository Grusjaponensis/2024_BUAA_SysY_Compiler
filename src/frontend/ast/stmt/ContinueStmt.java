package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.ASTNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.instr.IRJump;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code ContinueStmt -> 'continue' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class ContinueStmt extends ASTNode implements Statement {
    private int lineNum;

    public ContinueStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.ContinueKeyword, "continue");
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
        IRBuilder.getInstance().addInstr(
                new IRJump(IRBuilder.getInstance().getContinueBlock(), "continue")
        );
    }

    @Override
    public String toString() {
        addErrors();
        if (Debug.DEBUG_STATE) {
            return "  ".repeat(depth) + "<ContinueStmt> continue\n";
        }
        return "CONTINUETK continue\nSEMICN ;\n";
    }
}
