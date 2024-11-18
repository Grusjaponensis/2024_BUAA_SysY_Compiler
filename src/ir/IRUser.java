package ir;

import ir.type.IRType;

import java.util.ArrayList;

public abstract class IRUser extends IRValue {
    protected final ArrayList<IRValue> uses = new ArrayList<>();

    public IRUser(IRType type, String name) {
        super(type, name);
    }
}
