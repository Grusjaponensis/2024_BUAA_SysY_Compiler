package ir.instr;

import ir.IRValue;
import ir.type.IRPointerType;

public class IRLoad extends IRInstr {
    private final IRValue loadFrom;

    public IRLoad(String name, IRValue loadFrom) {
        // auto dereference
        super(((IRPointerType) loadFrom.type()).getObjectType(), name, IRInstrType.Load);
        this.loadFrom = loadFrom;
    }

    public IRLoad(String name, IRValue loadFrom, String message) {
        super(((IRPointerType) loadFrom.type()).getObjectType(), name, IRInstrType.Load, message);
        this.loadFrom = loadFrom;
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", super.name, super.type, loadFrom.type(), loadFrom.name());
    }

}
