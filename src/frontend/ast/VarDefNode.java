package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

/**
 * {@code VarDef -> Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal}
 */
public class VarDefNode extends ASTNode {
    public enum Type {
        SimpleVarNoInit, SimpleVarWithInit,
        ArrayNoInit, ArrayWithInit
    }
    private Type type;
    private Ident identifier;
    private ConstExpNode constExp;
    private InitValNode initVal;
    private int lineNum;

    public VarDefNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        boolean isArray = false;
        if (!token.isTypeOf(TokenType.Identifier)) {
            throw new RuntimeException("Identifier expected, got: " + token.getType());
        }
        identifier = new Ident(token.getContent());
        lineNum = token.getLineNumber();
        tokens.advance();

        if (tokens.get().isTypeOf(TokenType.LBracket)) {
            // array like init
            isArray = true;
            tokens.advance();
            constExp = new ConstExpNode(tokens, depth + 1);
            constExp.parse();
            expect(TokenType.RBracket, "]");
        }
        if (tokens.get().isTypeOf(TokenType.AssignOperator)) {
            // with initialization
            tokens.advance();
            type = isArray ? Type.ArrayWithInit : Type.SimpleVarWithInit;
            initVal = new InitValNode(tokens, depth + 1);
            initVal.parse();
        } else {
            type = isArray ? Type.ArrayNoInit : Type.SimpleVarNoInit;
        }
    }

    public void analyzeSemantic(SymbolTable table, BType type) {
        table.insert(new Var(lineNum, identifier.name(), type.valueType(), false, constExp != null));

        if (constExp != null) {
            constExp.analyzeSemantic(table);
        }
        if (initVal != null) {
            initVal.analyzeSemantic(table);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<VarDef> ");
            switch (type) {
                case SimpleVarNoInit -> b.append(identifier).append("\n");
                case SimpleVarWithInit -> b.append(identifier).append("\n").append(initVal);
                case ArrayNoInit -> b.append(identifier).append("[]\n");
                case ArrayWithInit -> b.append(identifier).append("[]\n").append(constExp).append(initVal);
            }
            return b.toString();
        }
        switch (type) {
            case SimpleVarNoInit -> b.append(identifier);
            case SimpleVarWithInit -> b.append(identifier).append("ASSIGN =\n").append(initVal);
            case ArrayNoInit -> b.append(identifier).append("LBRACK [\n").append(constExp).append("RBRACK ]\n");
            case ArrayWithInit ->
                    b.append(identifier).append("LBRACK [\n").append(constExp)
                            .append("RBRACK ]\n").append("ASSIGN =\n").append(initVal);
        }
        b.append("<VarDef>\n");
        return b.toString();
    }
}
