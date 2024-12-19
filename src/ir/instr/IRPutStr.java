package ir.instr;

import backend.Reg;
import backend.instr.MIPSLoadAddr;
import backend.instr.MIPSLoadImm;
import backend.instr.MIPSSyscall;
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

    @Override
    public void generateObjectCode() {
        // note that IRString::name does not have a leading '@'!
        new MIPSLoadAddr(Reg.a0, value.getName(), annotate());
        new MIPSLoadImm(Reg.v0, 4, annotate());
        new MIPSSyscall("putstr()");
    }

    @Override
    public String toString() {
        return String.format("call void @putstr(i8* %s)", target.name());
    }
}
