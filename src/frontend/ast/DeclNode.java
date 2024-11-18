package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

/**
 * Declaration base node.
 * </br>
 * {@code Decl -> ConstDecl | VarDecl}
 */
public class DeclNode extends ASTNode {
    private ConstDeclNode constNode;
    private VarDeclNode varNode;

    public enum Type {
        Const, Var
    }

    private Type type;

    public DeclNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.ConstKeyword)) {
            // const value
            constNode = new ConstDeclNode(tokens, depth + 1);
            constNode.parse();
            this.type = Type.Const;
        } else if (token.isTypeOf(TokenType.IntKeyword) || token.isTypeOf(TokenType.CharKeyword)) {
            varNode = new VarDeclNode(tokens, depth + 1);
            varNode.parse();
            this.type = Type.Var;
        } else {
            throw new RuntimeException("Neither ConstDecl nor VarDecl found, got: " + token.getType());
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        if (type == Type.Const) {
            constNode.analyzeSemantic(table);
        } else {
            varNode.analyzeSemantic(table);
        }
    }

    public void generateIR(SymbolTable table, boolean isGlobal) {
        if (type == Type.Const) {
            constNode.generateIR(table, isGlobal);
        } else {
            varNode.generateIR(table, isGlobal);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<Decl>\n").append(type == Type.Const ? constNode : varNode);
            return b.toString();
        }
        b.append(type == Type.Const ? constNode : varNode); // <Decl> doesn't need output
        return b.toString();
    }
}
