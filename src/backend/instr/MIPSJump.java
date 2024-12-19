package backend.instr;

import backend.Reg;

public class MIPSJump extends MIPSInstr {
    private String label;
    private Reg returnAddr;
    private final boolean isJr;

    /**
     * for {@code j / jal}
     */
    public MIPSJump(MIPSInstrType type, String label, String message) {
        super(type, false, message);
        this.label = label;
        isJr = false;
    }

    /**
     * for {@code jr}
     */
    public MIPSJump(MIPSInstrType type, Reg returnAddr, String message) {
        super(type, false, message);
        this.returnAddr = returnAddr;
        isJr = true;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() {
        return String.format("%-6s %s", super.type, isJr ? returnAddr : label);
    }
}
