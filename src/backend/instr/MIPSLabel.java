package backend.instr;

public class MIPSLabel extends MIPSInstr {
    private final String name;

    public MIPSLabel(String name, String message) {
        super(MIPSInstrType.Label, false, message);
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ": ";
    }
}
