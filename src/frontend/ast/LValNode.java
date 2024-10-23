package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code LVal -> Ident [ '[' Exp ']' ]}
 */
public class LValNode extends ASTNode {
    private Ident identifier;
    private ExpNode expNode;
    private int lineNum;

    public LValNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.Identifier)) {
            throw new RuntimeException("Identifier expected, got: " + token.getType());
        }
        identifier = new Ident(token.getContent());
        lineNum = token.getLineNumber();
        tokens.advance();
        if (tokens.get().isTypeOf(TokenType.LBracket)) {
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            expNode.parse();
            expect(TokenType.RBracket, "]");
        }
    }

    public boolean analyzeSemantic(SymbolTable table) {
        // check before use
        if (!table.hasSymbol(identifier.name())) {
            ErrorCollector.getInstance().addError(
                    new CompileError(lineNum, ErrorType.UndefinedSymbol,
                            "undefined identifier " + identifier.name())
            );
            return false;
        }
        if (expNode != null) {
            table.enterLValBracket();
            expNode.analyzeSemantic(table);
            table.exitLValBracket();
        }
        return true;
    }

    public String getName() { return identifier.name(); }

    public int getLineNum() { return lineNum; }

    /**
     * <pre> {@code
     * int a[10] = {1, 2, 3};
     * a;       // isArrayCall() == false
     * a[0];    // isArrayCall() == true
     * }</pre>
     * @return {@code true} if this lVal is called like an array.
     */
    public boolean isArrayCall() { return expNode != null; }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<LVal> ").append(identifier).append("\n");
            if (expNode != null) {
                b.append(expNode);
            }
            return b.toString();
        }
        b.append(identifier);
        if (expNode != null) {
            b.append("LBRACK [\n").append(expNode).append("RBRACK ]\n");
        }
        b.append("<LVal>\n");
        return b.toString();
    }
}
