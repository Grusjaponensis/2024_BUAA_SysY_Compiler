package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSBranch;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSJump;
import ir.IRBasicBlock;
import ir.IRBuilder;
import ir.IRValue;
import ir.type.IRBasicType;

public class IRBranch extends IRInstr {
    private final IRValue condition;
    private final IRBasicBlock destTrue;
    private final IRBasicBlock destFalse;

    public IRBranch(IRValue condition, IRBasicBlock destTrue, IRBasicBlock destFalse) {
        super(IRBasicType.Void, "", IRInstrType.Branch);
        this.condition = condition;
        this.destTrue = destTrue;
        this.destFalse = destFalse;
        destTrue.addParent(IRBuilder.getInstance().currentBlock());
        destFalse.addParent(IRBuilder.getInstance().currentBlock());
    }

    public IRBranch(IRValue condition, IRBasicBlock destTrue, IRBasicBlock destFalse, String message) {
        super(IRBasicType.Void, "", IRInstrType.Branch, message);
        this.condition = condition;
        this.destTrue = destTrue;
        this.destFalse = destFalse;
        destTrue.addParent(IRBuilder.getInstance().currentBlock());
        destFalse.addParent(IRBuilder.getInstance().currentBlock());
    }

    @Override
    public boolean use(IRValue value) { return false; }

    @Override
    public void generateObjectCode() {
        Reg condition = MIPSBuilder.getInstance().prepareRegForOperand(this.condition, Reg.t8);
        // if condition is false, branch to falseBlock; else jump to trueBlock unconditionally
        new MIPSBranch(MIPSInstrType.Beq, condition, Reg.zero, destFalse.name(), annotate());
        new MIPSJump(MIPSInstrType.J, destTrue.name(), annotate());
    }

    @Override
    public String toString() {
        return String.format(
                "br i1 %s, label %%%s, label %%%s",
                condition.name(), destTrue.name(), destFalse.name()
        );
    }
}
