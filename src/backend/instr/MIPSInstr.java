package backend.instr;

import backend.MIPSBuilder;

public abstract class MIPSInstr implements Comparable<MIPSInstr> {
    protected final MIPSInstrType type;
    public final String message;

    /**
     * Construct a new mips instr with its type and helpful message.
     */
    public MIPSInstr(MIPSInstrType type, boolean isData, String message) {
        this.type = type;
        this.message = message;
        if (isData) {
            MIPSBuilder.getInstance().addDataInstr(this);
        } else {
            MIPSBuilder.getInstance().addTextInstr(this);
        }
    }

    @Override
    public int compareTo(MIPSInstr o) { return type.compareTo(o.type); }

    public MIPSInstrType getType() { return type; }
}
