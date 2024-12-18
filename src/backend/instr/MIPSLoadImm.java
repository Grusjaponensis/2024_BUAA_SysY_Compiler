package backend.instr;

import backend.Reg;

public class MIPSLoadImm extends MIPSInstr {
    private final Reg dest;
    private final int immediate;

    public MIPSLoadImm(Reg dest, int immediate, String message) {
        super(MIPSInstrType.Li, false, message);
        this.dest = dest;
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return String.format("li     %s, %s", dest, immediate);
    }
}
