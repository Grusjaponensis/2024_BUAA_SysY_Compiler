package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;

/**
 * <p>
 * Arithmetic operations.<br>
 * {@code add, sub, mul, sdiv, srem}
 * </p>
 */
public class IRArithmetic extends IRInstr {
    private final IRValue operand1;
    private final IRValue operand2;

    /**
     * In each arithmetic operation, return value is always {@code i32}
     */
    public IRArithmetic(String name, IRInstrType instrType,
                        IRValue operand1, IRValue operand2) {
        super(IRBasicType.I32, name, instrType);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * In each arithmetic operation, return value is always {@code i32}
     */
    public IRArithmetic(String name, IRInstrType instrType,
                        IRValue operand1, IRValue operand2, String message) {
        super(IRBasicType.I32, name, instrType, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * e.g. {@code %3 = add i32 %1, %2}
     */
    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s, %s",
                super.name,                         // return v-reg
                instrType.toString().toLowerCase(), // instrType (add / sub / ...)
                operand1.type(),                    // op1 type (i32..)
                operand1.name(),
                operand2.name()
        );
    }
}
