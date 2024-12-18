package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSLoadImm;
import backend.instr.MIPSMemory;
import backend.instr.MIPSSyscall;
import ir.type.IRBasicType;

public class IRInput extends IRInstr {
    public IRInput(String name, IRInstrType instrType) {
        super(IRBasicType.I32, name, instrType);
    }

    public IRInput(String name, IRInstrType instrType, String message) {
        super(IRBasicType.I32, name, instrType, message);
    }

    @Override
    public void generateObjectCode() {
        if (super.instrType == IRInstrType.GetChar) {
            new MIPSLoadImm(Reg.v0, 12, "");
            new MIPSSyscall("getchar()");
            new MIPSMemory(MIPSInstrType.Sw, Reg.v0, Reg.sp, MIPSBuilder.getInstance().stackPush(this, 4), annotate());
        } else {
            // getint()
            new MIPSLoadImm(Reg.v0, 5, "");
            new MIPSSyscall("getint()");
            new MIPSMemory(MIPSInstrType.Sw, Reg.v0, Reg.sp, MIPSBuilder.getInstance().stackPush(this, 4), annotate());
        }
    }

    @Override
    public String toString() {
        return String.format("%s = call i32 @%s()", super.name, instrType.toString().toLowerCase());
    }
}
