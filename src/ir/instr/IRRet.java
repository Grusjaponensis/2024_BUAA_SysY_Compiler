package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSJump;
import backend.instr.MIPSMove;
import ir.IRValue;
import ir.type.IRBasicType;
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
    public void generateObjectCode() {
        if (super.type != IRBasicType.Void) {
            Reg returnReg = MIPSBuilder.getInstance().prepareRegForOperand(returnValue, Reg.v0);
            if (returnReg != Reg.v0) {
                new MIPSMove(Reg.v0, returnReg, annotate());
            }
        }
        new MIPSJump(MIPSInstrType.Jr, Reg.ra, annotate());
    }

    @Override
    public String toString() {
        return String.format("ret %s", (returnValue == null ? "void" : (returnValue.type() + " " + returnValue.name())));
    }
}
