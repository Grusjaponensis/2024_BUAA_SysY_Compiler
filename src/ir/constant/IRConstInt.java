package ir.constant;

import ir.type.IRType;

import static ir.type.IRBasicType.*;

public class IRConstInt extends IRConst {
    private final int value;

    public IRConstInt(IRType type, int value) {
        super(type, String.valueOf(value));
        assert type == I32 || type == I8 || type == I1;
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return type.toString() + " " + value;
    }
}
