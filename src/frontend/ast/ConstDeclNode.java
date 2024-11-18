package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * Constant declaration.
 * </br>
 * {@code ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'}</br>
 * <p>Possible error: missing semicolon</p>
 */
public class ConstDeclNode extends ASTNode {
    private BType bType;
    private final ArrayList<ConstDefNode> constDefNodes = new ArrayList<>();

    public ConstDeclNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        expect(TokenType.ConstKeyword, "const");
        Token token = tokens.get();
        bType = new BType(
                token.getType() == TokenType.IntKeyword ? TokenType.IntKeyword : token.getType()
        );
        tokens.advance();

        ConstDefNode constDefNode = new ConstDefNode(tokens, depth + 1);
        constDefNode.parse();
        constDefNodes.add(constDefNode);
        while (tokens.get().isTypeOf(TokenType.Comma)) {
            tokens.advance();
            constDefNode = new ConstDefNode(tokens, depth + 1);
            constDefNode.parse();
            constDefNodes.add(constDefNode);
        }
        expect(TokenType.Semicolon, ";");
    }

    public void analyzeSemantic(SymbolTable table) {
        constDefNodes.forEach(constDef -> constDef.analyzeSemantic(table, bType));
    }

    public void generateIR(SymbolTable table, boolean isGlobal) {
        constDefNodes.forEach(constDef -> constDef.generateIR(table, isGlobal));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<ConstDecl> ").append("const ").append(bType).append("\n");
            constDefNodes.forEach(b::append);
            return b.toString();
        }
        b.append("CONSTTK const\n").append(bType).append(constDefNodes.get(0));
        for (int i = 1; i < constDefNodes.size(); i++) {
            b.append("COMMA ,\n").append(constDefNodes.get(i));
        }
        b.append("SEMICN ;\n").append("<ConstDecl>\n");
        return b.toString();
    }
}
