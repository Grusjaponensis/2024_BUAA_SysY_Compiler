package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;

public class IRPutInt extends IRInstr {
    private final IRValue value;

    public IRPutInt(IRValue value) {
        super(IRBasicType.Void, "", IRInstrType.PutInt);
        this.value = value;
    }

    public IRPutInt(IRValue value, String message) {
        super(IRBasicType.Void, "", IRInstrType.PutInt, message);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("call void @putint(i32 %s)", value.name());
    }
}
