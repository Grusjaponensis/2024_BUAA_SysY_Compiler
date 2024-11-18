package frontend.ast;

import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.constant.IRConstArray;
import ir.constant.IRConstInt;
import ir.instr.*;
import ir.type.IRArrayType;
import ir.type.IRBasicType;
import ir.type.IRType;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.Var;
import util.Debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * Define a constant
 * </br>
 * {@code ConstDef -> Ident [ '[' ConstExp ']' ] '=' ConstInitVal}
 */
public class ConstDefNode extends ASTNode {
    private Ident identifier;
    private ConstExpNode constExpNode;
    private ConstInitValNode constInitValNode;
    private int lineNum;

    public ConstDefNode(TokenList tokens, int depth) {
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
            // array initialization
            tokens.advance();
            constExpNode = new ConstExpNode(tokens, depth + 1);
            constExpNode.parse();
            expect(TokenType.RBracket, "]");
        }
        expect(TokenType.AssignOperator, "=");
        constInitValNode = new ConstInitValNode(tokens, depth + 1);
        constInitValNode.parse();
    }

    public void analyzeSemantic(SymbolTable table, BType type) {
        table.insert(new Var(lineNum, identifier.name(), type.valueType(), true, constExpNode != null));

        if (constExpNode != null) {
            constExpNode.analyzeSemantic(table);
        }

        constInitValNode.analyzeSemantic(table);
    }

    public void generateIR(SymbolTable table, boolean isGlobal) {
        if (isGlobal) {
            generateGlobalConst(table);
        } else {
            generateLocalConst(table);
        }
    }

    private void generateGlobalConst(SymbolTable table) {
        Symbol symbol = table.find(identifier.name());
        IRGlobal globalVar;
        Var var = (Var) symbol;
        IRType contentType = var.getValueType().mapToIRType();
        if (constExpNode != null) {
            // global const array
            int arraySize = constExpNode.calculateConstVal(table);
            // init values set to all-0 manually
            ArrayList<Integer> initValues = new ArrayList<>(Collections.nCopies(arraySize, 0));
            // actual init values
            ArrayList<Integer> partInitValues = constInitValNode.getInitValueArray(table);
            // reset those values which has been explicitly assigned
            IntStream.range(0, partInitValues.size()).forEach(i -> initValues.set(i, partInitValues.get(i)));
            if (contentType == IRBasicType.I8) {
                IntStream.range(0, arraySize).forEach(i -> initValues.set(i, initValues.get(i) & 0xff));
            }
            var.setArrayInitVal(initValues);
            globalVar = new IRGlobal(
                    new IRArrayType(arraySize, contentType),
                    var.getName(), true,
                    new IRConstArray(contentType, initValues)
            );
        } else {
            // single const
            int initVal = constInitValNode.getSingleInitValue(table);
            if (contentType == IRBasicType.I8) {
                initVal &= 0xff;
            }
            var.setInitVal(initVal);
            globalVar = new IRGlobal(contentType, var.getName(), true, new IRConstInt(contentType, initVal));
        }
        symbol.setIrValue(globalVar);
        IRBuilder.getInstance().addGlobalVar(globalVar);
        Debug.log("[Add global constant]:\n" + globalVar);
    }

    private void generateLocalConst(SymbolTable table) {
        Symbol symbol = table.find(identifier.name());
        Var var  = (Var) symbol;
        IRInstr localVar;
        IRType contentType = var.getValueType().mapToIRType();
        if (constExpNode != null) {
            // local const array
            int arraySize = constExpNode.calculateConstVal(table);
            ArrayList<Integer> initValues = new ArrayList<>(Collections.nCopies(arraySize, 0));
            ArrayList<Integer> partInitValues = constInitValNode.getInitValueArray(table);
            IntStream.range(0, partInitValues.size()).forEach(i -> initValues.set(i, partInitValues.get(i)));
            if (contentType == IRBasicType.I8) {
                IntStream.range(0, arraySize).forEach(i -> initValues.set(i, initValues.get(i) & 0xff));
            }
            var.setArrayInitVal(initValues);
            localVar = new IRAlloca(new IRArrayType(arraySize, contentType), IRBuilder.getInstance().localReg(), "alloca: " + var.getName());
            IRBuilder.getInstance().addInstr(localVar);
            for (int i = 0; i < arraySize; i++) {
                // first get the ptr to the element
                IRGetElemPtr elemPtr = new IRGetElemPtr(
                        contentType, IRBuilder.getInstance().localReg(), localVar, new IRConstInt(contentType, i), "getElemPtr of " + var.getName()
                );
                IRBuilder.getInstance().addInstr(elemPtr);
                IRBuilder.getInstance().addInstr(
                        new IRStore(new IRConstInt(contentType, initValues.get(i)), elemPtr)
                );
            }
        } else {
            int initVal = constInitValNode.getSingleInitValue(table);
            if (contentType == IRBasicType.I8) {
                initVal &= 0xff;
            }
            var.setInitVal(initVal);
            // when generating local vars, should use auto increment number as virtual register name
            localVar = new IRAlloca(contentType, IRBuilder.getInstance().localReg(), "alloca: " + var.getName());
            IRBuilder.getInstance().addInstr(localVar);
            // local vars using alloca and store instr to store value
            IRBuilder.getInstance().addInstr(
                    new IRStore(new IRConstInt(contentType, initVal), localVar, "store: " + initVal + " -> " + var.getName())
            );
        }
        symbol.setIrValue(localVar);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<ConstDef> ").append(identifier);
            if (constExpNode != null) {
                b.append("[").append("]");
            }
            b.append("\n").append(constInitValNode);
            return b.toString();
        }
        b.append(identifier);
        if (constExpNode != null) {
            b.append("LBRACK [\n").append(constExpNode).append("RBRACK ]\n");
        }
        b.append("ASSIGN =\n").append(constInitValNode).append("<ConstDef>\n");
        return b.toString();
    }
}
