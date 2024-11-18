package ir.constant;

import ir.type.IRType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRConstArray extends IRConst {
    private final ArrayList<IRConstInt> initVals = new ArrayList<>();

    /**
     * Create a new const array with init values.
     * @param type element type of this array
     * @param initVals notice that all values must be initialized to 0 manually.
     */
    public IRConstArray(IRType type, ArrayList<Integer> initVals) {
        super(type, "array_" + type);
        initVals.forEach(i -> this.initVals.add(new IRConstInt(type, i)));
    }

    public ArrayList<Integer> getInitVals() {
        return initVals.stream()
                .map(IRConstInt::getValue)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        if (initVals.isEmpty()) {
            return "zeroinitializer";
        }
        return "[" + initVals.stream()
                .map(IRConstInt::toString)
                .collect(Collectors.joining(", "))
                + "]";
    }
}
