package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.ast.LValNode;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

/**
 * {@code LValAssignStmt -> }</br>
 * {@code LVal '=' Exp ';' }</br>
 * {@code | LVal '=' 'getint''('')'';' }</br>
 * {@code | LVal '=' 'getchar''('')'';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class LValAssignStmt extends ASTNode implements Statement {
    public enum Type {
        Exp, GetInt, GetChar
    }
    private Type type;
    private LValNode lVal;
    private ExpNode exp;

    public LValAssignStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        lVal = new LValNode(tokens, depth + 1);
        lVal.parse();

        expect(TokenType.AssignOperator, "=");

        Token token = tokens.get();
        if (token.isTypeOf(TokenType.GetCharKeyword)) {
            type = Type.GetChar;
            expect(TokenType.GetCharKeyword, "getchar");
            expect(TokenType.LParenthesis, "(");
            expect(TokenType.RParenthesis, ")");
        } else if (token.isTypeOf(TokenType.GetIntKeyword)) {
            type = Type.GetInt;
            expect(TokenType.GetIntKeyword, "getint");
            expect(TokenType.LParenthesis, "(");
            expect(TokenType.RParenthesis, ")");
        } else {
            type = Type.Exp;
            exp = new ExpNode(tokens, depth + 1);
            exp.parse();
        }
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        // check before use
        if (!lVal.analyzeSemantic(table)) {
            // if this lVal is undefined
            return;
        }
        // check if it is a constant
        if (((Var) table.find(lVal.getName())).isConst()) {
            ErrorCollector.getInstance().addError(
                    new CompileError(lVal.getLineNum(), ErrorType.ChangeConstValue,
                            "cannot assign to constant variable " + lVal.getName())
            );
        }
        if (type == Type.Exp) {
            exp.analyzeSemantic(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<LValAssign>\n");
            switch (type) {
                case Exp -> b.append(lVal).append("\n").append(exp);
                case GetInt -> b.append(lVal).append(" = ").append("getint()\n");
                case GetChar -> b.append(lVal).append(" = ").append("getchar()\n");
            }
            return b.toString();
        }
        switch (type) {
            case Exp -> b.append(lVal).append("ASSIGN =\n").append(exp).append("SEMICN ;\n");
            case GetInt -> b.append(lVal).append("ASSIGN =\n")
                    .append("GETINTTK getint\nLPARENT (\nRPARENT )\nSEMICN ;\n");
            case GetChar -> b.append(lVal).append("ASSIGN =\n")
                    .append("GETCHARTK getchar\nLPARENT (\nRPARENT )\nSEMICN ;\n");
        }
        return b.toString();
    }
}
