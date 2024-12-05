package ir.instr;

import backend.instr.MIPSAscii;
import ir.IRValue;
import ir.constant.IRString;
import ir.type.IRBasicType;

public class IRPutStr extends IRInstr {
    private final IRValue target;
    private final IRString value;

    public IRPutStr(IRValue target, IRString value) {
        super(IRBasicType.Void, "", IRInstrType.PutStr);
        this.target = target;
        this.value = value;
    }

    public IRPutStr(IRValue target, IRString value, String message) {
        super(IRBasicType.Void, "", IRInstrType.PutStr, message);
        this.target = target;
        this.value = value;
    }

    public void generateObjectCode() {
        // note that IRString::name does not have a leading '@'!
    }

    @Override
    public String toString() {
        return String.format("call void @putstr(i8* %s)", target.name());
    }
}
