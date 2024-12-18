package backend.instr;

import backend.Reg;

public class MIPSCmp extends MIPSInstr {
    private final Reg dest;
    private final Reg operand1;
    private final Reg operand2;

    public MIPSCmp(MIPSInstrType type, Reg dest, Reg operand1, Reg operand2, String message) {
        super(type, false, message);
        this.dest = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s, %s, %s", super.type, dest, operand1, operand2);
    }
}
