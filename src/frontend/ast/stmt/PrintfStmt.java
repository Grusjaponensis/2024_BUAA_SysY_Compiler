package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.ast.StringConst;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code PrintfStmt -> 'printf' '(' StringConst { ',' Exp } ')' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class PrintfStmt extends ASTNode implements Statement {
    private StringConst formattedString;
    private final ArrayList<ExpNode> params = new ArrayList<>();

    public PrintfStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.PrintfKeyword, "printf");
        expect(TokenType.LParenthesis, "(");

        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.StringLiteral)) {
            throw new RuntimeException("String literal expected, got: " + token);
        }
        formattedString = new StringConst(token.getContent());
        tokens.advance();

        if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
            while (tokens.get().isTypeOf(TokenType.Comma)) {
                tokens.advance();
                ExpNode param = new ExpNode(tokens, depth + 1);
                param.parse();
                params.add(param);
            }
        }
        expect(TokenType.RParenthesis, ")");
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<PrintfStmt>\n")
                    .append("  ".repeat(depth + 1)).append(formattedString).append("\n");
            params.forEach(b::append);
            return b.toString();
        }
        b.append("PRINTFTK printf\nLPARENT (\n").append(formattedString);
        params.forEach(e -> b.append("COMMA ,\n").append(e));
        b.append("RPARENT )\n").append("SEMICN ;\n");
        return b.toString();
    }
}
