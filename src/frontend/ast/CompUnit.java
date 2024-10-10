package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Root AST node for a program.
 * </br>
 * {@code CompUnit -> {Decl} | {FuncDef} | MainFuncDef}
 */
public class CompUnit extends ASTNode {
    private final List<DeclNode> declNodes = new ArrayList<>();
    private final List<FuncDefNode> funcDefNodes = new ArrayList<>();
    private MainFuncDefNode mainFuncDefNode;

    public CompUnit(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get(), token2 = tokens.get(2);
        // declaration
        while (isDecl(token) && !token2.isTypeOf(TokenType.LParenthesis)) {
            DeclNode declNode = new DeclNode(tokens, depth + 1);
            declNode.parse();
            declNodes.add(declNode);
            token = tokens.get();
            token2 = tokens.get(2);
        }

        // funcDef
        while (token2.isTypeOf(TokenType.LParenthesis) && !tokens.get(1).isTypeOf(TokenType.MainKeyword)) {
            FuncDefNode funcDefNode = new FuncDefNode(tokens, depth + 1);
            funcDefNode.parse();
            funcDefNodes.add(funcDefNode);
            token2 = tokens.get(2);
        }

        // Main func
        this.mainFuncDefNode = new MainFuncDefNode(tokens, depth + 1);
        mainFuncDefNode.parse();
    }

    private boolean isDecl(Token token) {
        return token.isTypeOf(TokenType.ConstKeyword) ||
                token.isTypeOf(TokenType.IntKeyword) ||
                token.isTypeOf(TokenType.CharKeyword);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            b.append("<CompUnit>\n");
            declNodes.forEach(b::append);
            funcDefNodes.forEach(b::append);
            b.append(mainFuncDefNode);
            return b.toString();
        }
        declNodes.forEach(b::append);
        funcDefNodes.forEach(b::append);
        b.append(mainFuncDefNode).append("<CompUnit>\n");
        return b.toString();
    }
}
