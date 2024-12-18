package backend.instr;

import backend.Reg;

public class MIPSMove extends MIPSInstr {
    private final Reg dst;
    private final Reg src;

    public MIPSMove(Reg dst, Reg src, String message) {
        super(MIPSInstrType.Move, false, message);
        this.dst = dst;
        this.src = src;
    }

    @Override
    public String toString() {
        return String.format("move   %s, %s", dst, src);
    }
}
