package backend.instr;

import backend.Reg;

public class MIPSLoadAddr extends MIPSInstr {
    private final Reg dest;
    private final String label;

    public MIPSLoadAddr(Reg dest, String label, String message) {
        super(MIPSInstrType.La, false, message);
        this.dest = dest;
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("la     %s, %s", dest, label);
    }
}
