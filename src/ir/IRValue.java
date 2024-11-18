package ir;

import ir.type.IRType;

public abstract class IRValue {
    protected final IRType type;
    protected final String name;

    public IRValue(IRType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IRType type() {
        return type;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() { return type + " " + name; }
}
