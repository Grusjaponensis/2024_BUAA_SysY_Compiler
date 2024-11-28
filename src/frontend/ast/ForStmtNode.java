package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.instr.IRStore;
import ir.instr.IRTypeCast;
import ir.type.IRPointerType;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

/**
 * {@code ForStmt -> LVal '=' Exp}
 */
public class ForStmtNode extends ASTNode {
    private LValNode lVal;
    private ExpNode exp;

    public ForStmtNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        lVal = new LValNode(tokens, depth + 1);
        lVal.parse();

        expect(TokenType.AssignOperator, "=");

        exp = new ExpNode(tokens, depth + 1);
        exp.parse();
    }

    public void analyzeSemantic(SymbolTable table) {
        if (table.find(lVal.getName()) instanceof Var var) {
            if (var.isConst()) {
                ErrorCollector.getInstance().addError(
                        new CompileError(lVal.getLineNum(), ErrorType.ChangeConstValue)
                );
            }
        }
        exp.analyzeSemantic(table);
    }

    public void generateIR(SymbolTable table) {
        // probably as same as 'LVal Assignment Node'
        Symbol symbol = table.find(lVal.getName());
        // the allocated ptr
        IRValue ptr = symbol.getIrValue();
        IRValue value;
        if (lVal.isArrayCall()) {
            // update ptr to use elemPtr
            ptr = lVal.generateIR(table, true);
        }
        value = this.exp.generateIR(table);
        //.type check
        if (value.type() != ((IRPointerType) ptr.type()).getObjectType()) {
            value = IRTypeCast.typeCast(value, ptr);
        }
        IRBuilder.getInstance().addInstr(
                new IRStore(value, ptr, "store: " + value.name() + " -> " + symbol.getName())
        );
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ForStmt>\n").append(lVal).append("\n").append(exp);
            return b.toString();
        }
        b.append(lVal).append("ASSIGN =\n").append(exp).append("<ForStmt>\n");
        return b.toString();
    }
}
