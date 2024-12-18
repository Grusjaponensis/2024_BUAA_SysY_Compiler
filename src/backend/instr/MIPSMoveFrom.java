package backend.instr;

import backend.Reg;

public class MIPSMoveFrom extends MIPSInstr {
    private final Reg dest;

    public MIPSMoveFrom(MIPSInstrType type, Reg dest, String message) {
        super(type, false, message);
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s", type, dest);
    }
}
