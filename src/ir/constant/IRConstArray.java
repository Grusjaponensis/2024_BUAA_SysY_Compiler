package ir.constant;

import ir.type.IRBasicType;
import ir.type.IRType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRConstArray extends IRConst {
    private final IRType elemType;
    private final ArrayList<IRConstInt> initVals = new ArrayList<>();

    /**
     * Create a new const array with init values.
     * @param type element type of this array
     * @param initVals notice that all values must be initialized to 0 manually.
     */
    public IRConstArray(IRType type, ArrayList<Integer> initVals, IRType elemType) {
        super(type, "array_" + type);
        initVals.forEach(i -> this.initVals.add(new IRConstInt(type, i)));
        this.elemType = elemType;
    }

    public ArrayList<Integer> getInitVals() {
        return initVals.stream()
                .map(IRConstInt::getValue)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public IRType getElemType() { return elemType; }

    @Override
    public String toString() {
        return "[" + initVals.stream()
                .map(IRConstInt::toString)
                .collect(Collectors.joining(", "))
                + "]";
    }
}
