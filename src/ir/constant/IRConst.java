package ir.constant;

import ir.IRUser;
import ir.type.IRType;

public abstract class IRConst extends IRUser {
    public IRConst(IRType type, String name) {
        super(type, name);
    }
}
