package symbol;

import java.util.ArrayList;

public class Func extends Symbol {
    private final ValueType returnType;
    private ArrayList<ValueType> paramTypes;
    private ArrayList<Boolean> isParamsArray;

    public Func(int lineNum, String name, ValueType returnType,
                ArrayList<ValueType> paramTypes, ArrayList<Boolean> isParamsArray) {
        super(lineNum, name, SymbolType.Function);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.isParamsArray = isParamsArray;
    }

    public int getParamsNum() { return paramTypes.size(); }

    public ArrayList<Boolean> getIsParamsArray() { return isParamsArray; }

    public ArrayList<ValueType> getParamTypes() { return paramTypes; }

    public ValueType getReturnType() { return returnType; }

    public void setParamTypes(ArrayList<ValueType> paramTypes) { this.paramTypes = paramTypes; }

    public void setIsParamsArray(ArrayList<Boolean> isParamsArray) { this.isParamsArray = isParamsArray; }
}
