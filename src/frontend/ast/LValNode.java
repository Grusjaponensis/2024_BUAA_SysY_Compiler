package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.instr.IRGetElemPtr;
import ir.instr.IRInstr;
import ir.instr.IRLoad;
import ir.instr.IRTypeCast;
import ir.type.IRArrayType;
import ir.type.IRBasicType;
import ir.type.IRPointerType;
import ir.type.IRType;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.Var;
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
            if (!(table.find(identifier.name()) instanceof Var)) {
                ErrorCollector.getInstance().addError(
                        new CompileError(lineNum, ErrorType.UndefinedSymbol,
                                "undefined identifier " + identifier.name())
                );
                return false;
            }
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

    public int evaluate(SymbolTable table) {
        Symbol var = table.find(identifier.name());
        if (expNode != null) {
            // as an array element
            int index = expNode.evaluate(table);
            return ((Var) var).getArrayInitVal().get(index);
        } else {
            return ((Var) var).getInitVal();
        }
    }

    public IRValue generateIR(SymbolTable table, boolean isAssignment) {
        Symbol symbol = table.find(identifier.name());
        if (expNode != null) {
            // fetch array item, so type of this symbol must be a pointer
            IRValue offset = expNode.generateIR(table);
            IRInstr elemPtr;
            IRType elemType = symbol.getIrValue().type();
            if (((IRPointerType) elemType).getObjectType() instanceof IRPointerType) {
                // this symbol came from func param, so first load (dereference)
                IRInstr loadFromPointer = new IRLoad(IRBuilder.getInstance().localReg(), symbol.getIrValue(), "load: " + symbol.getName());
                IRBuilder.getInstance().addInstr(loadFromPointer);
                assert loadFromPointer.type() instanceof IRPointerType;
                elemPtr = new IRGetElemPtr(
                        ((IRPointerType) loadFromPointer.type()).getObjectType(),
                        IRBuilder.getInstance().localReg(), loadFromPointer, offset
                );
            } else {
                // array type
                elemPtr = new IRGetElemPtr(
                        ((IRArrayType) ((IRPointerType) elemType).getObjectType()).getElementType(),
                        IRBuilder.getInstance().localReg(), symbol.getIrValue(), offset, "ptr: " + identifier.name() + "[" + offset.name() + "]"
                );
            }
            IRBuilder.getInstance().addInstr(elemPtr);
            if (isAssignment) {
                // for assignment like 'a[0] = 1', just need the elemPtr to a[0], so return in advance
                return elemPtr;
            }
            IRInstr loadValue = new IRLoad(IRBuilder.getInstance().localReg(), elemPtr, "load: " + identifier.name());
            IRBuilder.getInstance().addInstr(loadValue);
            // since we only need i32 in an exp, so simply zext all i8 to i32
            return typeCheck(loadValue);
        }
        // single var or directly call array identifier (as a func parameter)
        if (symbol instanceof Var var && var.isArray()) {
            // call array identifier
            return generateIRForArrayIdent(var);
        }
        // single var
        IRInstr loadValue = new IRLoad(IRBuilder.getInstance().localReg(), symbol.getIrValue(), "load: " + symbol.getName());
        IRBuilder.getInstance().addInstr(loadValue);
        // since we only need i32 in an exp, so simply zext all i8 to i32
        return typeCheck(loadValue);
    }

    private IRValue generateIRForArrayIdent(Var array) {
        IRInstr elemPtr = new IRGetElemPtr(
                array.getValueType().mapToIRType(),
                IRBuilder.getInstance().localReg(),
                array.getIrValue(),
                new IRConstInt(IRBasicType.I32, 0),
                "ptr: " + array.getName()
        );
        IRBuilder.getInstance().addInstr(elemPtr);
        return elemPtr;
    }

    private IRValue typeCheck(IRValue load) {
        if (load.type() == IRBasicType.I8) {
            return IRTypeCast.typeCast(load, IRBasicType.I32);
        }
        return load;
    }

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
