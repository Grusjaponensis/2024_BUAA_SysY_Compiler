package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.type.IRBasicType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * {@code InitVal -> Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst}
 */
public class InitValNode extends ASTNode {
    public enum Type {
        SimpleExp, MultipleExp, StrConst
    }
    private Type type;
    private ExpNode expNode;
    private final ArrayList<ExpNode> expList = new ArrayList<>();
    private StringConst strConst;

    public InitValNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (token.isTypeOf(TokenType.StringLiteral)) {
            tokens.advance();
            type = Type.StrConst;
            strConst = new StringConst(token.getContent());
        } else if (token.isTypeOf(TokenType.LBrace)) {
            tokens.advance();
            type = Type.MultipleExp;
            if (!tokens.get().isTypeOf(TokenType.RBrace)) {
                ExpNode node = new ExpNode(tokens, depth + 1);
                node.parse();
                expList.add(node);
                while (tokens.get().isTypeOf(TokenType.Comma)) {
                    tokens.advance();
                    node = new ExpNode(tokens, depth + 1);
                    node.parse();
                    expList.add(node);
                }
            }
            expect(TokenType.RBrace, "}");
        } else {
            type = Type.SimpleExp;
            expNode = new ExpNode(tokens, depth + 1);
            expNode.parse();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        switch (type) {
            case SimpleExp -> expNode.analyzeSemantic(table);
            case MultipleExp -> expList.forEach(exp -> exp.analyzeSemantic(table));
        }
    }

    public int getSingleConstInitValue(SymbolTable table) {
        return expNode.evaluate(table);
    }

    public ArrayList<Integer> getConstInitValueArray(SymbolTable table) {
        if (type == Type.MultipleExp) {
            return expList.stream()
                    .map(node -> node.evaluate(table))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return strConst.value().chars().boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    public IRValue getSingleInitVal(SymbolTable table) {
        return expNode.generateIR(table);
    }

    public ArrayList<IRValue> getInitValArray(SymbolTable table) {
        if (type == Type.MultipleExp) {
            return expList.stream()
                    .map(node -> node.generateIR(table))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        // using a sequence of store
        ArrayList<IRValue> irValues = new ArrayList<>();
        for (char c : strConst.value().toCharArray()) {
            irValues.add(new IRConstInt(IRBasicType.I8, c));
        }
        // append '\0' to initVals
        irValues.add(new IRConstInt(IRBasicType.I8, 0));
        return irValues;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<InitVal>\n");
            switch (type) {
                case SimpleExp -> b.append(expNode);
                case MultipleExp -> expList.forEach(b::append);
                case StrConst -> b.append(strConst);
            }
            return b.toString();
        }
        switch (type) {
            case SimpleExp -> b.append(expNode);
            case MultipleExp -> {
                b.append("LBRACE {\n").append(expList.get(0));
                for (int i = 1; i < expList.size(); i++) {
                    b.append("COMMA ,\n").append(expList.get(i));
                }
                b.append("RBRACE }\n");
            }
            case StrConst -> b.append(strConst);
        }
        b.append("<InitVal>\n");
        return b.toString();
    }
}
