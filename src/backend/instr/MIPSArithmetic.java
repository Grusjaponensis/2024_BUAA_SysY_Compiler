package backend.instr;

import backend.Reg;

public class MIPSArithmetic extends MIPSInstr {
    private Reg dest;
    private final Reg operand1;
    private Reg operand2;
    private int immediate;
    private enum Type {
        // Instruction of type I (addi / addiu), type R (add / sub), type M_D (Mult / Div)
        I, R, M_D
    }
    private final Type type;

    /**
     * {@code rd <= rs <operator> rt}
     */
    public MIPSArithmetic(MIPSInstrType type, Reg dest, Reg operand1, Reg operand2, String message) {
        super(type, false, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.dest = dest;
        this.type = Type.R;
    }

    /**
     * {@code rt <= rs <operator> immediate}
     */
    public MIPSArithmetic(MIPSInstrType type, Reg dest, Reg operand1, int immediate, String message) {
        super(type, false, message);
        this.operand1 = operand1;
        this.dest = dest;
        this.immediate = immediate;
        this.type = Type.I;
    }

    /**
     * Arithmetic operations for multiply / division / modulo
     */
    public MIPSArithmetic(MIPSInstrType type, Reg operand1, Reg operand2, String message) {
        super(type, false, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.type = Type.M_D;
    }

    /**
     * e.g. {@code add   $t0, $t1, $t2} / {@code addi  $t0, $t1, 100}
     */
    @Override
    public String toString() {
        if (type == Type.I) {
            return String.format("%-6s %s, %s, %s", super.type, dest, operand1, immediate);
        } else if (type == Type.R) {
            return String.format("%-6s %s, %s, %s", super.type, dest, operand1, operand2);
        } else {
            return String.format("%-6s %s, %s", super.type, operand1, operand2);
        }
    }
}
