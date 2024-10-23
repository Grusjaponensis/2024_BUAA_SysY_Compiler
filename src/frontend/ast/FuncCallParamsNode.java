package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.Func;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code FuncCallParams -> Exp { ',' Exp }}</br>
 * aka. {@code FuncRParams}
 */
public class FuncCallParamsNode extends ASTNode {
    private final ArrayList<ExpNode> params = new ArrayList<>();
    private int lineNum;

    public FuncCallParamsNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        ExpNode expNode = new ExpNode(tokens, depth + 1);
        expNode.parse();
        params.add(expNode);
        lineNum = tokens.prev().getLineNumber();

        while (tokens.get().isTypeOf(TokenType.Comma)) {
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            expNode.parse();
            params.add(expNode);
        }
    }

    public void analyzeSemantic(SymbolTable table, String name) {
        // check the number of parameters
        Func func = (Func) table.find(name);
        if (func.getParamsNum() != params.size()) {
            ErrorCollector.getInstance().addError(
                    new CompileError(lineNum, ErrorType.ParamNumMismatch,
                            "expected " + func.getParamsNum() + " found " + params.size())
            );
            // if the number of parameter is wrong, then ignore all checks below
            return;
        }
        // record current function for further analysis
        table.setFuncToCall(func);
        params.forEach(exp -> {
            exp.analyzeSemantic(table);
            table.incrementParamsIndex();
        });
        // unset current function
        table.setFuncToCall(null);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space);
            b.append("<FuncCallParams>\n");
            params.forEach(b::append);
            b.append("\n");
            return b.toString();
        }
        b.append(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            b.append("COMMA ,\n").append(params.get(i));
        }
        b.append("<FuncRParams>\n");
        return b.toString();
    }
}
