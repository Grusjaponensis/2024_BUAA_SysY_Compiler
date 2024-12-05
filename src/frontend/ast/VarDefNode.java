package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.constant.IRConstArray;
import ir.constant.IRConstInt;
import ir.instr.*;
import ir.type.*;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

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

    public void generateIR(SymbolTable table, boolean isGlobal) {
        if (isGlobal) {
            generateGlobalVar(table);
        } else {
            generateLocalVar(table);
        }
    }

    private void generateGlobalVar(SymbolTable table) {
        Symbol symbol = table.find(identifier.name());
        Var var = (Var) symbol;
        IRGlobal globalVar;
        IRType contentType = var.getValueType().mapToIRType();
        if (constExp != null) {
            // array
            int arraySize = constExp.evaluate(table);
            ArrayList<Integer> initVals = new ArrayList<>(Collections.nCopies(arraySize, 0));
            if (initVal != null) {
                // has init values
                ArrayList<Integer> partInitVals = initVal.getConstInitValueArray(table);
                IntStream.range(0, partInitVals.size()).forEach(i -> initVals.set(i, partInitVals.get(i)));
            }
            if (contentType == IRBasicType.I8) {
                // type cast
                IntStream.range(0, arraySize).forEach(i -> initVals.set(i, initVals.get(i) & 0xff));
            }
            var.setArrayInitVal(initVals);
            globalVar = new IRGlobal(
                    new IRArrayType(arraySize, contentType),
                    var.getName(), false,
                    new IRConstArray(contentType, initVals, contentType)
            );
        } else {
            int initValue = 0;
            if (this.initVal != null) {
                initValue = initVal.getSingleConstInitValue(table);
            }
            if (contentType == IRBasicType.I8) {
                initValue &= 0xff;
            }
            var.setInitVal(initValue);
            globalVar = new IRGlobal(contentType, var.getName(), false, new IRConstInt(contentType, initValue));
        }
        symbol.setIrValue(globalVar);
        IRBuilder.getInstance().addGlobalVar(globalVar);
    }

    private void generateLocalVar(SymbolTable table) {
        Symbol symbol = table.find(identifier.name());
        Var var = (Var) symbol;
        IRType contentType = var.getValueType().mapToIRType();
        if (constExp != null) {
            // local array
            int arraySize = constExp.evaluate(table);
            IRInstr alloca = new IRAlloca(
                    new IRArrayType(arraySize, contentType), IRBuilder.getInstance().localReg(), "alloca: " + var.getName()
            );
            IRBuilder.getInstance().addInstr(alloca);
            symbol.setIrValue(alloca);
            if (initVal != null) {
                ArrayList<IRValue> initVals = initVal.getInitValArray(table);
                for (int i = 0; i < initVals.size(); i++) {
                    IRValue initValue_i = initVals.get(i);
                    IRInstr elemPtr = new IRGetElemPtr(
                            // pointer type will automatically add by the constructor
                            contentType, IRBuilder.getInstance().localReg(),
                            alloca, new IRConstInt(contentType, i), "ptr: " + var.getName() + "[" + i + "]"
                    );
                    IRBuilder.getInstance().addInstr(elemPtr);
                    // type check
                    if (initValue_i.type() != ((IRPointerType) elemPtr.type()).getObjectType()) {
                        initValue_i = IRTypeCast.typeCast(initValue_i, elemPtr);
                    }
                    IRBuilder.getInstance().addInstr(new IRStore(initValue_i, elemPtr));
                }
            }
        } else {
            IRInstr alloca = new IRAlloca(contentType, IRBuilder.getInstance().localReg(), "alloca: " + var.getName());
            IRBuilder.getInstance().addInstr(alloca);
            symbol.setIrValue(alloca);
            if (initVal != null) {
                IRValue initValue = initVal.getSingleInitVal(table);
                // type check
                if (initValue.type() != ((IRPointerType) alloca.type()).getObjectType()) {
                    initValue = IRTypeCast.typeCast(initValue, alloca);
                }
                IRBuilder.getInstance().addInstr(
                        new IRStore(initValue, alloca, "store: " + initValue.name() + " -> " + var.getName())
                );
            }
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
