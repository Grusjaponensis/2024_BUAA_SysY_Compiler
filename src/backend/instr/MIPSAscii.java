package backend.instr;

/**
 * {@code ascii} in MIPS instruction means a <strong>non-zero terminated</strong> string literal.
 */
public class MIPSAscii extends MIPSInstr {
    private final String name;
    private final String value;

    public MIPSAscii(String name, String value, String message) {
        super(MIPSInstrType.Ascii, true, message);
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s:%n\t.ascii   \"%s\"", name, value);
    }
}
