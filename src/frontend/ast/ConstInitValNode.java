package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * {@code ConstInitVal -> ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst}
 */
public class ConstInitValNode extends ASTNode {
    public enum Type {
        SingleConstExp, ArrayLike, StringConst
    }

    private ConstExpNode constExp;
    private final ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
    private StringConst stringConst;
    private boolean isBlockEmpty = false;
    private Type type;

    public ConstInitValNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.LBrace)) {
            // array const initial value(s)
            tokens.advance();
            type = Type.ArrayLike;
            if (tokens.get().isTypeOf(TokenType.RBrace)) {
                // empty initial value
                isBlockEmpty = true;
                tokens.advance();
            } else {
                ConstExpNode constExp = new ConstExpNode(tokens, depth + 1);
                constExp.parse();
                constExpNodes.add(constExp);
                while (tokens.get().isTypeOf(TokenType.Comma)) {
                    tokens.advance();
                    constExp = new ConstExpNode(tokens, depth + 1);
                    constExp.parse();
                    constExpNodes.add(constExp);
                }
                expect(TokenType.RBrace, "}");
            }
        } else if (token.isTypeOf(TokenType.StringLiteral)) {
            // string literal initial value
            type = Type.StringConst;
            stringConst = new StringConst(token.getContent());
            tokens.advance();
        } else {
            // single exp initial value
            type = Type.SingleConstExp;
            constExp = new ConstExpNode(tokens, depth + 1);
            constExp.parse();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        switch (type) {
            case SingleConstExp -> constExp.analyzeSemantic(table);
            case ArrayLike -> constExpNodes.forEach(exp -> exp.analyzeSemantic(table));
        }
    }

    public int getSingleInitValue(SymbolTable table) {
        return constExp.evaluate(table);
    }

    public ArrayList<Integer> getInitValueArray(SymbolTable table) {
        if (type == Type.ArrayLike) {
            return constExpNodes.stream()
                    .map(node -> node.evaluate(table))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return stringConst.value().chars().boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<ConstInitVal>\n");
            switch (type) {
                case SingleConstExp -> b.append(constExp);
                case ArrayLike -> constExpNodes.forEach(b::append);
                case StringConst -> b.append(stringConst);
            }
            return b.toString();
        }
        switch (type) {
            case SingleConstExp -> b.append(constExp);
            case ArrayLike -> {
                b.append("LBRACE {\n");
                if (!isBlockEmpty) {
                    b.append(constExpNodes.get(0));
                    for (int i = 1; i < constExpNodes.size(); i++) {
                        b.append("COMMA ,\n").append(constExpNodes.get(i));
                    }
                }
                b.append("RBRACE }\n");
            }
            case StringConst -> b.append(stringConst);
        }
        b.append("<ConstInitVal>\n");
        return b.toString();
    }
}
