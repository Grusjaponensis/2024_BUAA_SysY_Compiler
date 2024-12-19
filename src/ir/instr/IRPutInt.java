package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSLoadImm;
import backend.instr.MIPSMove;
import backend.instr.MIPSSyscall;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.type.IRBasicType;

public class IRPutInt extends IRInstr {
    private IRValue value;

    public IRPutInt(IRValue value) {
        super(IRBasicType.Void, "", IRInstrType.PutInt);
        this.value = value;
        this.uses.add(value);
    }

    public IRPutInt(IRValue value, String message) {
        super(IRBasicType.Void, "", IRInstrType.PutInt, message);
        this.value = value;
        this.uses.add(value);
    }

    @Override
    public void generateObjectCode() {
        if (value instanceof IRConstInt constInt) {
            new MIPSLoadImm(Reg.a0, constInt.getValue(), annotate());
        } else {
            Reg regForIRValue = MIPSBuilder.getInstance().prepareRegForOperand(value, Reg.t8);
            new MIPSMove(Reg.a0, regForIRValue, annotate());
        }
        new MIPSLoadImm(Reg.v0, 1, annotate());
        new MIPSSyscall("putint()");
    }

    @Override
    public void replaceUse(IRValue value, IRValue newValue) {
        if (this.value == value) {
            this.value = newValue;
        }
        this.uses.replaceAll(oldValue -> oldValue == value ? newValue : oldValue);
    }

    @Override
    public String toString() {
        return String.format("call void @putint(i32 %s)", value.name());
    }
}
