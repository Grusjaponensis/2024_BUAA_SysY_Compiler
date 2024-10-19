package symbol;

import java.util.ArrayList;

public class Var extends Symbol {
    private final ValueType type;
    private final boolean isArray;
    /// Init value of single variable.
    private int initVal;
    /// Array init value consists of Char or Int, where these int values represent ASCII value or itself.
    private ArrayList<Integer> arrayInitVal;

    /**
     * Create a <strong>non-array</strong> variable.
     */
    public Var(int lineNum, String name, ValueType type, boolean isArray) {
        super(lineNum, name, SymbolType.Variable);
        this.type = type;
        this.isArray = isArray;
    }

    public void setInitVal(int initVal) { this.initVal = initVal; }

    public void setArrayInitVal(ArrayList<Integer> arrayInitVal) { this.arrayInitVal = arrayInitVal; }

    public ValueType getValueType() { return this.type; }

    public int getInitVal() { return initVal; }

    public ArrayList<Integer> getArrayInitVal() { return arrayInitVal; }

    public boolean isArray() { return isArray; }
}
