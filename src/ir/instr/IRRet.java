package ir.instr;

import ir.IRValue;
import ir.type.IRType;

public class IRRet extends IRInstr {
    private final IRValue returnValue;

    public IRRet(IRType valueType, IRValue returnValue) {
        super(valueType, "", IRInstrType.Ret);
        this.returnValue = returnValue;
    }

    public IRRet(IRType valueType, IRValue returnValue, String message) {
        super(valueType, "", IRInstrType.Ret, message);
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return String.format("ret %s", (returnValue == null ? "void" : (returnValue.type() + " " + returnValue.name())));
    }
}
