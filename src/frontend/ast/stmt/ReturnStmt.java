package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import symbol.ValueType;
import util.Debug;

/**
 * {@code ReturnStmt -> 'return' [Exp] ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class ReturnStmt extends ASTNode implements Statement {
    private ExpNode returnExp;
    private int lineNum;

    public ReturnStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.ReturnKeyword, "return");
        lineNum = tokens.prev().getLineNumber();
        if (isExpr(tokens.get().getType())) {
            returnExp = new ExpNode(tokens, depth + 1);
            returnExp.parse();
        }
        expect(TokenType.Semicolon, ";");
    }

    private boolean isExpr(TokenType type) {
        return type == TokenType.PlusOperator ||
                type == TokenType.MinusOperator ||
                type == TokenType.NotOperator ||
                type == TokenType.LParenthesis ||
                type == TokenType.Identifier ||
                type == TokenType.IntLiteral ||
                type == TokenType.CharLiteral;
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        if (table.inInFuncDef() && table.getFuncReturnType() == ValueType.Void) {
            // if this return statement is inside a void function, check if it has invalid return value
            if (returnExp != null) {
                ErrorCollector.getInstance().addError(
                        new CompileError(lineNum, ErrorType.VoidReturnValue)
                );
            }
        }
        if (returnExp != null) {
            returnExp.analyzeSemantic(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ReturnStmt>\n");
            if (returnExp != null) {
                b.append(returnExp);
            }
            return b.toString();
        }
        b.append("RETURNTK return\n");
        if (returnExp != null) {
            b.append(returnExp);
        }
        b.append("SEMICN ;\n");
        return b.toString();
    }
}
