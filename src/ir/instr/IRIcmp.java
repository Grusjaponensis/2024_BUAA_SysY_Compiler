package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSCmp;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.IRValue;
import ir.type.IRBasicType;

public class IRIcmp extends IRInstr {
    private IRValue operand1;
    private IRValue operand2;

    public IRIcmp(String name, IRInstrType instrType, IRValue operand1, IRValue operand2) {
        super(IRBasicType.I1, name, instrType);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.uses.add(operand1);
        this.uses.add(operand2);
    }

    public IRIcmp(String name, IRInstrType instrType,
                  IRValue operand1, IRValue operand2, String message) {
        super(IRBasicType.I1, name, instrType, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.uses.add(operand1);
        this.uses.add(operand2);
    }

    private void generateComparisonOperations(Reg dest, Reg operand1, Reg operand2) {
        MIPSInstrType type = switch (this.instrType) {
            case Eq -> MIPSInstrType.Seq;
            case Ne -> MIPSInstrType.Sne;
            case Sle -> MIPSInstrType.Sle;
            case Slt -> MIPSInstrType.Slt;
            case Sge -> MIPSInstrType.Sge;
            case Sgt -> MIPSInstrType.Sgt;
            default -> throw new RuntimeException("Illegal cmp operator");
        };
        new MIPSCmp(type, dest, operand1, operand2, annotate());
    }

    @Override
    public void generateObjectCode() {
        Reg operand1 = MIPSBuilder.getInstance().prepareRegForOperand(this.operand1, Reg.t8);
        Reg operand2 = MIPSBuilder.getInstance().prepareRegForOperand(this.operand2, Reg.t9);
        Reg dest = Reg.t8;

        generateComparisonOperations(dest, operand1, operand2);
        new MIPSMemory(
                MIPSInstrType.Sw,
                dest, Reg.sp,
                MIPSBuilder.getInstance().stackPush(this, 4),
                annotate()
        );
    }

    @Override
    public void replaceUse(IRValue value, IRValue newValue) {
        if (this.operand1 == value) {
            this.operand1 = newValue;
        }
        if (this.operand2 == value) {
            this.operand2 = newValue;
        }
        this.uses.replaceAll(oldValue -> oldValue == value ? newValue : oldValue);
    }

    /**
     * e.g. {@code %2 = icmp eq i32 %1, 0}
     */
    @Override
    public String toString() {
        return String.format(
                "%s = icmp %s %s %s, %s",
                super.name, instrType.toString().toLowerCase(),
                operand1.type(), operand1.name(), operand2.name()
        );
    }
}
