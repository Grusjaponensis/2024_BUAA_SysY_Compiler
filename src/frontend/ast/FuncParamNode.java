package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.instr.IRAlloca;
import ir.instr.IRInstr;
import ir.instr.IRStore;
import ir.type.IRType;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

/**
 * {@code FuncFParam -> BType Ident [ '[' ']' ]}
 */
public class FuncParamNode extends ASTNode {
    private boolean isArray = false;
    private BType paramType;
    private Ident identifier;
    private int lineNum;

    public FuncParamNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.IntKeyword) && !token.isTypeOf(TokenType.CharKeyword)) {
            throw new RuntimeException("Undefined parameter type: " + token);
        }
        paramType = new BType(token.getType());
        tokens.advance();

        token = tokens.get();
        if (!token.isTypeOf(TokenType.Identifier)) {
            throw new RuntimeException("Identifier expected, got: " + token);
        }
        identifier = new Ident(token.getContent());
        lineNum = token.getLineNumber();
        tokens.advance();

        token = tokens.get();
        if (token.isTypeOf(TokenType.LBracket)) {
            tokens.advance();
            expect(TokenType.RBracket, "]");
            isArray = true;
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        table.insert(new Var(lineNum, identifier.name(), paramType.valueType(), false, isArray));
    }

    public void generateIR(SymbolTable table) {
        IRType type;
        if (!isArray) {
            type = this.paramType.valueType().mapToIRType();
            IRInstr alloca = new IRAlloca(type, IRBuilder.getInstance().localReg());
            IRBuilder.getInstance().addInstr(alloca);
            IRBuilder.getInstance().addInstr(new IRStore(table.find(identifier.name()).getIrValue(), alloca));
            table.find(identifier.name()).setIrValue(alloca);
        }
        // if this param is an array (e.g. int a[]), use the IRFuncParam directly
        // so here we don't need any code
    }

    public BType getType() { return paramType; }

    public boolean isArray() { return isArray; }

    public String getName() { return identifier.name(); }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<FuncFParam> ").append(paramType).append(" ").append(identifier);
            if (isArray) {
                b.append("[]");
            }
            b.append("\n");
            return b.toString();
        }
        b.append(paramType).append(identifier);
        if (isArray) {
            b.append("LBRACK [\n").append("RBRACK ]\n");
        }
        b.append("<FuncFParam>\n");
        return b.toString();
    }
}
