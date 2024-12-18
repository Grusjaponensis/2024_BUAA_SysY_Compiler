package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.IRValue;
import ir.type.IRBasicType;

public class IRStore extends IRInstr {
    private final IRValue src;
    private final IRValue dst;

    public IRStore(IRValue src, IRValue dst) {
        // store don't have a name because it doesn't have a return value
        super(IRBasicType.Void, "", IRInstrType.Store);
        this.src = src;
        this.dst = dst;
    }

    public IRStore(IRValue src, IRValue dst, String message) {
        super(IRBasicType.Void, "", IRInstrType.Store, message);
        this.src = src;
        this.dst = dst;
    }

    @Override
    public void generateObjectCode() {
        Reg src = MIPSBuilder.getInstance().prepareRegForOperand(this.src, Reg.t9);
        Reg dst = MIPSBuilder.getInstance().prepareRegForPointer(this.dst, Reg.t8);
        if (this.src.type() == IRBasicType.I8) {
            new MIPSMemory(MIPSInstrType.Sb, src, dst, 0, annotate());
        } else {
            new MIPSMemory(MIPSInstrType.Sw, src, dst, 0, annotate());
        }
    }

    /**
     * e.g. {@code store i32 %1, i32* %2}
     */
    @Override
    public String toString() {
        return "store " + src.type() + " " + src.name() + ", " + dst.type() + " " + dst.name();
    }
}
