package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRFuncParam;
import ir.IRValue;
import ir.type.IRPointerType;
import ir.type.IRType;
import symbol.SymbolTable;
import symbol.ValueType;
import util.Debug;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * {@code FuncFParams -> FuncFParam { ',' FuncFParam }}</br>
 */
public class FuncParamListNode extends ASTNode {
    private FuncParamNode funcParam;
    private final ArrayList<FuncParamNode> otherFuncParams = new ArrayList<>();

    public FuncParamListNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        funcParam = new FuncParamNode(tokens, depth + 1);
        funcParam.parse();

        while (tokens.get().isTypeOf(TokenType.Comma)) {
            tokens.advance();
            FuncParamNode node = new FuncParamNode(tokens, depth + 1);
            node.parse();
            otherFuncParams.add(node);
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        funcParam.analyzeSemantic(table);
        otherFuncParams.forEach(param -> param.analyzeSemantic(table));
    }

    public ArrayList<ValueType> getParamTypes() {
        return collectParamAttributes(param -> param.getType().valueType());
    }

    public ArrayList<Boolean> isParamsArray() {
        return collectParamAttributes(FuncParamNode::isArray);
    }

    private <T> ArrayList<T> collectParamAttributes(Function<FuncParamNode, T> extractor) {
        ArrayList<T> paramValues = new ArrayList<>();
        paramValues.add(extractor.apply(funcParam));
        otherFuncParams.forEach(param -> paramValues.add(extractor.apply(param)));
        return paramValues;
    }

    public ArrayList<IRValue> getParamsIRValues(SymbolTable table) {
        ArrayList<IRValue> paramValues = new ArrayList<>();
        // FIXME: a piece of shit, fuck everybody
        IRType type = funcParam.isArray() ?
                new IRPointerType(funcParam.getType().valueType().mapToIRType()) :
                funcParam.getType().valueType().mapToIRType();
        IRValue param = new IRFuncParam(type, "%" + funcParam.getName());
        paramValues.add(param);
        table.find(funcParam.getName()).setIrValue(param);
        for (FuncParamNode node : otherFuncParams) {
            type = node.isArray() ?
                    new IRPointerType(node.getType().valueType().mapToIRType()) :
                    node.getType().valueType().mapToIRType();
            param = new IRFuncParam(type, "%" + node.getName());
            table.find(node.getName()).setIrValue(param);
            paramValues.add(param);
        }
        return paramValues;
    }

    public void generateIR(SymbolTable table) {
        funcParam.generateIR(table);
        otherFuncParams.forEach(param -> param.generateIR(table));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<FuncRParam>\n");
            b.append(funcParam);
            otherFuncParams.forEach(b::append);
            return b.toString();
        }
        b.append(funcParam);
        otherFuncParams.forEach(param -> b.append("COMMA ,\n").append(param));
        b.append("<FuncFParams>\n");
        return b.toString();
    }
}
