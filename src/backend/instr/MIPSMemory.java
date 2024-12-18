package backend.instr;

import backend.Reg;

public class MIPSMemory extends MIPSInstr {
    private final Reg reg;
    private final Reg baseAddr;
    private final int offset;

    public MIPSMemory(MIPSInstrType type, Reg reg, Reg baseAddr, int offset, String message) {
        super(type, false, message);
        this.reg = reg;
        this.baseAddr = baseAddr;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s, %s(%s)", type, reg, offset, baseAddr);
    }
}
