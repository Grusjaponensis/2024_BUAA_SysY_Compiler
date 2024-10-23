package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

/**
 * Define a constant
 * </br>
 * {@code ConstDef -> Ident [ '[' ConstExp ']' ] '=' ConstInitVal}
 */
public class ConstDefNode extends ASTNode {
    private Ident identifier;
    private ConstExpNode constExpNode;
    private ConstInitValNode constInitValNode;
    private int lineNum;

    public ConstDefNode(TokenList tokens, int depth) {
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
            // array initialization
            tokens.advance();
            constExpNode = new ConstExpNode(tokens, depth + 1);
            constExpNode.parse();
            expect(TokenType.RBracket, "]");
        }
        expect(TokenType.AssignOperator, "=");
        constInitValNode = new ConstInitValNode(tokens, depth + 1);
        constInitValNode.parse();
    }

    public void analyzeSemantic(SymbolTable table, BType type) {
        table.insert(new Var(lineNum, identifier.name(), type.valueType(), true, constExpNode != null));

        if (constExpNode != null) {
            constExpNode.analyzeSemantic(table);
        }

        constInitValNode.analyzeSemantic(table);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<ConstDef> ").append(identifier);
            if (constExpNode != null) {
                b.append("[").append("]");
            }
            b.append("\n").append(constInitValNode);
            return b.toString();
        }
        b.append(identifier);
        if (constExpNode != null) {
            b.append("LBRACK [\n").append(constExpNode).append("RBRACK ]\n");
        }
        b.append("ASSIGN =\n").append(constInitValNode).append("<ConstDef>\n");
        return b.toString();
    }
}
