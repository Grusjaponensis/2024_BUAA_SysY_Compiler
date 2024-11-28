package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;

public class IRIcmp extends IRInstr {
    private final IRValue operand1;
    private final IRValue operand2;

    public IRIcmp(String name, IRInstrType instrType, IRValue operand1, IRValue operand2) {
        super(IRBasicType.I1, name, instrType);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public IRIcmp(String name, IRInstrType instrType,
                  IRValue operand1, IRValue operand2, String message) {
        super(IRBasicType.I1, name, instrType, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
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
