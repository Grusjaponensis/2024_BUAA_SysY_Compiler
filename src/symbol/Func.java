package symbol;

import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Func extends Symbol {
    private final ValueType returnType;
    private ArrayList<ValueType> paramTypes = new ArrayList<>();
    private ArrayList<Boolean> isParamsArray = new ArrayList<>();

    public Func(int lineNum, String name, ValueType returnType,
                ArrayList<ValueType> paramTypes, ArrayList<Boolean> isParamsArray) {
        super(lineNum, name);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.isParamsArray = isParamsArray;
    }

    /**
     * Use when func def has no parameters.
     */
    public Func(int lineNum, String name, ValueType returnType) {
        super(lineNum, name);
        this.returnType = returnType;
    }

    public int getParamsNum() { return paramTypes.size(); }

    public ArrayList<Boolean> getIsParamsArray() { return isParamsArray; }

    public ArrayList<ValueType> getParamTypes() { return paramTypes; }

    public ValueType getReturnType() { return returnType; }

    @Override
    public String toString() {
        if (!Debug.DEBUG_STATE) {
            return " " + name + " " + returnType + "Func\n";
        }
        String s = "[" + IntStream.range(0, isParamsArray.size())
                .mapToObj(i -> paramTypes.get(i) + ": " + isParamsArray.get(i))
                .collect(Collectors.joining(", ")) + "]";
        return String.format("%s[Func  ]%s name: %-6s, type: %-6s, params: %-15s, declared: line %d%n",
                Debug.TERM_GREEN, Debug.TERM_RESET, name, returnType, s, lineNum);
    }
}
