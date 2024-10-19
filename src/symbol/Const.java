package symbol;

import java.util.ArrayList;

public class Const extends Symbol {
    private final ValueType type;
    private final boolean isArray;
    /// Init value of single variable.
    private int initVal = 0;
    /// Length of an array.
    /// Array init value consists of Char or Int, where these int values represent ASCII value or itself.
    private ArrayList<Integer> arrayInitVal;

    public Const(int lineNum, String name, ValueType type, boolean isArray) {
        super(lineNum, name, SymbolType.Constant);
        this.type = type;
        this.isArray = isArray;
    }

    public void setInitVal(int initVal) { this.initVal = initVal; }

    public void setArrayInitVal(ArrayList<Integer> arrayInitVal) { this.arrayInitVal = arrayInitVal; }

    public boolean isArray() { return isArray; }

    public int getInitVal() { return initVal; }

    public ArrayList<Integer> getArrayInitVal() { return arrayInitVal; }

    public ValueType getValueType() { return type; }
}
