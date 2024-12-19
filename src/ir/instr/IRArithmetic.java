package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSArithmetic;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import backend.instr.MIPSMoveFrom;
import ir.IRValue;
import ir.type.IRBasicType;

/**
 * <p>
 * Arithmetic operations.<br>
 * {@code add, sub, mul, sdiv, srem}
 * </p>
 */
public class IRArithmetic extends IRInstr {
    private IRValue operand1;
    private IRValue operand2;

    /**
     * In each arithmetic operation, return value is always {@code i32}
     */
    public IRArithmetic(String name, IRInstrType instrType,
                        IRValue operand1, IRValue operand2) {
        super(IRBasicType.I32, name, instrType);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.uses.add(operand1);
        this.uses.add(operand2);
    }

    /**
     * In each arithmetic operation, return value is always {@code i32}
     */
    public IRArithmetic(String name, IRInstrType instrType,
                        IRValue operand1, IRValue operand2, String message) {
        super(IRBasicType.I32, name, instrType, message);
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.uses.add(operand1);
        this.uses.add(operand2);
    }

    private void generateArithmeticOperations(Reg dest, Reg operand1, Reg operand2) {
        switch (this.instrType) {
            case Add -> new MIPSArithmetic(MIPSInstrType.Addu, dest, operand1, operand2, annotate());
            case Sub -> new MIPSArithmetic(MIPSInstrType.Subu, dest, operand1, operand2, annotate());
            case Mul -> {
                new MIPSArithmetic(MIPSInstrType.Mult, operand1, operand2, annotate());
                new MIPSMoveFrom(MIPSInstrType.Mflo, dest, String.format("result of mul: %s [%s]", this, this.message));
            }
            case SDiv -> {
                new MIPSArithmetic(MIPSInstrType.Div, operand1, operand2, annotate());
                new MIPSMoveFrom(MIPSInstrType.Mflo, dest, String.format("result of sdiv: %s [%s]", this, this.message));
            }
            case SRem -> {
                new MIPSArithmetic(MIPSInstrType.Div, operand1, operand2, annotate());
                new MIPSMoveFrom(MIPSInstrType.Mfhi, dest, String.format("result of srem: %s [%s]", this, this.message));
            }
            default -> throw new RuntimeException("Invalid arithmetic operation");
        }
    }

    @Override
    public void generateObjectCode() {
        // $t8 as a temp result, since no register is mapped
        Reg dest = Reg.t8;
        Reg operand1 = MIPSBuilder.getInstance().prepareRegForOperand(this.operand1, Reg.t8);
        Reg operand2 = MIPSBuilder.getInstance().prepareRegForOperand(this.operand2, Reg.t9);

        generateArithmeticOperations(dest, operand1, operand2);
        // store result
        new MIPSMemory(
                MIPSInstrType.Sw,
                Reg.t8, Reg.sp,
                MIPSBuilder.getInstance().stackPush(this, 4),
                annotate()
        );
    }

    public IRValue getOperand1() { return operand1; }

    public IRValue getOperand2() { return operand2; }

    @Override
    public void replaceUse(IRValue value, IRValue newValue) {
        if (this.operand1 == value) {
            this.operand1 = newValue;
        }
        if (this.operand2 == value) {
            this.operand2 = newValue;
        }
        this.uses.replaceAll(oldValue -> oldValue == value ? newValue : oldValue);
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
