package ir.instr;

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
    public String toString() {
        return String.format(
          "br i1 %s, label %%%s, label %%%s",
          condition.name(), destTrue.name(), destFalse.name()
        );
    }
}
