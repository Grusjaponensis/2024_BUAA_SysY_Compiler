package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code VarDecl -> BType VarDef { ',' VarDef } ';'}</br>
 * <p>Possible error: missing semicolon</p>
 */
public class VarDeclNode extends ASTNode {
    private BType bType;
    private final ArrayList<VarDefNode> varDefNodes = new ArrayList<>();

    public VarDeclNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        bType = new BType(token.getType());
        tokens.advance();

        VarDefNode varDefNode = new VarDefNode(tokens, depth + 1);
        varDefNode.parse();
        varDefNodes.add(varDefNode);
        while (tokens.get().isTypeOf(TokenType.Comma)) {
            tokens.advance();
            varDefNode = new VarDefNode(tokens, depth + 1);
            varDefNode.parse();
            varDefNodes.add(varDefNode);
        }
        expect(TokenType.Semicolon, ";");
    }

    public void analyzeSemantic(SymbolTable table) {
        varDefNodes.forEach(var -> var.analyzeSemantic(table, bType));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<VarDecl> ").append(bType).append("\n").append(varDefNodes.get(0));
            for (int i = 1; i < varDefNodes.size(); i++) {
                b.append(varDefNodes.get(i));
            }
            return b.toString();
        }
        b.append(bType).append(varDefNodes.get(0));
        for (int i = 1; i < varDefNodes.size(); i++) {
            b.append("COMMA ,\n").append(varDefNodes.get(i));
        }
        b.append("SEMICN ;\n").append("<VarDecl>\n");
        return b.toString();
    }
}
