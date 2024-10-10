package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.ast.LValNode;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
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
        try {
            lVal.parse();
        } catch (CompileError e) {
            ErrorCollector.getInstance().addError(e);
        }

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
            try {
                exp.parse();
            } catch (CompileError e) {
                ErrorCollector.getInstance().addError(e);
            }
        }
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
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
