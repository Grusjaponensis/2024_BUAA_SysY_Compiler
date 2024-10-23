package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
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
