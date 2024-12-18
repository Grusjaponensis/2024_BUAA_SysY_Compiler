package backend.instr;

import backend.Reg;

public class MIPSBranch extends MIPSInstr {
    private final Reg operand1;
    private final Reg operand2;
    private final String label;

    public MIPSBranch(MIPSInstrType type, Reg operand1, Reg operand2, String label, String message) {
        super(type, false, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s, %s, %s", super.type, operand1, operand2, label);
    }
}
