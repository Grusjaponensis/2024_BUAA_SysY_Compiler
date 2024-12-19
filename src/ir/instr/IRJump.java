package ir.instr;

import backend.instr.MIPSInstrType;
import backend.instr.MIPSJump;
import ir.IRBasicBlock;
import ir.IRBuilder;
import ir.IRValue;
import ir.type.IRBasicType;

public class IRJump extends IRInstr {
    IRBasicBlock dest;

    public IRJump(IRBasicBlock dest) {
        super(IRBasicType.Void, "", IRInstrType.Jump);
        this.dest = dest;
        dest.addParent(IRBuilder.getInstance().currentBlock());
    }

    public IRJump(IRBasicBlock dest, String message) {
        super(IRBasicType.Void, "", IRInstrType.Jump, message);
        this.dest = dest;
        dest.addParent(IRBuilder.getInstance().currentBlock());
    }

    @Override
    public boolean use(IRValue value) { return false; }

    @Override
    public void generateObjectCode() {
        new MIPSJump(MIPSInstrType.J, dest.name(), annotate());
    }

    @Override
    public String toString() {
        return "br label %" + dest.name();
    }
}
