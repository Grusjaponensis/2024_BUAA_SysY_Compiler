package symbol;

import util.Debug;

import java.util.ArrayList;

public class Var extends Symbol {
    private final ValueType type;
    private final boolean isConst;
    private final boolean isArray;
    /// Init value of single variable.
    private int initVal;
    /// Array init value consists of Char or Int, where these int values represent ASCII value or itself.
    private ArrayList<Integer> arrayInitVal;

    /**
     * Create a <strong>non-array</strong> variable.
     */
    public Var(int lineNum, String name, ValueType type, boolean isConst, boolean isArray) {
        super(lineNum, name);
        this.type = type;
        this.isConst = isConst;
        this.isArray = isArray;
    }

    public boolean isConst() { return this.isConst; }

    public void setInitVal(int initVal) { this.initVal = initVal; }

    public void setArrayInitVal(ArrayList<Integer> arrayInitVal) { this.arrayInitVal = arrayInitVal; }

    public ValueType getValueType() { return this.type; }

    public int getInitVal() { return initVal; }

    public ArrayList<Integer> getArrayInitVal() { return arrayInitVal; }

    public boolean isArray() { return isArray; }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return String.format("%s[%s]%s name: %s type: %s, declared: line %d%n",
                    Debug.TERM_GREEN, isConst ? "Const" : "Var", Debug.TERM_RESET, name, type + (isArray ? "[]" : ""), lineNum);
        }
        return " " + name + " " + (isConst ? "Const" : "") + type + (isArray ? "Array" : "") + "\n";
    }
}
