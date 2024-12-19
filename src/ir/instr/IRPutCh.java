package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSLoadImm;
import backend.instr.MIPSMove;
import backend.instr.MIPSSyscall;
import ir.IRValue;
import ir.type.IRBasicType;

public class IRPutCh extends IRInstr {
    private char c;
    private IRValue value;

    public IRPutCh(char c) {
        super(IRBasicType.Void, "", IRInstrType.PutCh);
        this.c = c;
        this.uses.add(value);
    }

    public IRPutCh(IRValue value) {
        super(IRBasicType.Void, "", IRInstrType.PutCh);
        this.value = value;
        this.uses.add(value);
    }

    @Override
    public void generateObjectCode() {
        new MIPSLoadImm(Reg.v0, 11, annotate());
        if (value == null) {
            // print a char literal
            new MIPSLoadImm(Reg.a0, c, annotate());
        } else {
            // print a IRValue
            Reg regForIRValue = MIPSBuilder.getInstance().prepareRegForOperand(value, Reg.t8);
            new MIPSMove(Reg.a0, regForIRValue, annotate());
        }
        new MIPSSyscall("putch()");
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
        if (value != null) {
            return String.format("call void @putch(i32 %s)", value.name());
        }
        return String.format("call void @putch(i32 %d)", (int) c);
    }
}
