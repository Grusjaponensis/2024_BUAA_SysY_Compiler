package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

/**
 * Define a function.
 * </br>
 * {@code FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block}
 */
public class FuncDefNode extends ASTNode {
    private FuncType funcType;
    private Ident identifier;
    private FuncParamListNode paramList;
    private BlockNode body;

    public FuncDefNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        // parse function type
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.VoidKeyword) &&
                !token.isTypeOf(TokenType.CharKeyword) &&
                !token.isTypeOf(TokenType.IntKeyword)) {
            throw new RuntimeException("Type expected, got: " + token.getType());
        }
        funcType = new FuncType(token.getType());
        tokens.advance();

        // parse function name
        token = tokens.get();
        if (!token.isTypeOf(TokenType.Identifier)) {
            throw new RuntimeException("Identifier expected, got: " + token.getType());
        }
        identifier = new Ident(token.getContent());
        tokens.advance();

        // parse function params
        expect(TokenType.LParenthesis, "(");
        if (tokens.get().isTypeOf(TokenType.IntKeyword) || tokens.get().isTypeOf(TokenType.CharKeyword)) {
            paramList = new FuncParamListNode(tokens, depth + 1);
            paramList.parse();
        }
        expect(TokenType.RParenthesis, ")");

        // parse function body
        body = new BlockNode(tokens, depth + 1);
        body.parse();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<FuncDef> ").append(funcType).append(" ").append(identifier).append("()\n");
            if (paramList != null) {
                b.append(paramList);
            }
            b.append(body);
            return b.toString();
        }
        b.append(funcType).append(identifier).append("LPARENT (\n");
        if (paramList != null) {
            b.append(paramList);
        }
        b.append("RPARENT )\n").append(body).append("<FuncDef>\n");
        return b.toString();
    }
}
