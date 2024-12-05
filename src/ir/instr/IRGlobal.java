package ir.instr;

import backend.instr.MIPSAscii;
import backend.instr.MIPSByte;
import backend.instr.MIPSWord;
import frontend.token.StringLiteral;
import ir.constant.IRConst;
import ir.constant.IRConstArray;
import ir.constant.IRConstInt;
import ir.type.IRBasicType;
import ir.type.IRPointerType;
import ir.type.IRType;

import java.util.stream.Collectors;

/**
 * <p>
 * Allocate a global var or global constant.<br>
 * <strong>Notice</strong>: global vars and constants are <b>Pointer</b> types.
 * </p>
 */
public class IRGlobal extends IRInstr {
    protected final boolean isConst;
    /// content type
    protected final IRType objectType;
    protected final IRConst initVal;

    /**
     * Create a new global var with init value(s).
     * @param type global var type (integer / array of integer)
     * @param name the result v-register to store the return pointer
     * @param initVal init value(s).
     */
    public IRGlobal(IRType type, String name, boolean isConst, IRConst initVal) {
        super(new IRPointerType(type), "@" + name, IRInstrType.Global);
        this.isConst = isConst;
        this.objectType = type;
        this.initVal = initVal;
    }

    /**
     * Create a new global var with init value(s).
     * @param type global var type (integer / array of integer)
     * @param name the result v-register to store the return pointer
     * @param initVal init value(s).
     * @param message debug message
     */
    public IRGlobal(IRType type, String name, boolean isConst, IRConst initVal, String message) {
        super(new IRPointerType(type), name, IRInstrType.Global, message);
        this.isConst = isConst;
        this.objectType = type;
        this.initVal = initVal;
    }

    public void generateObjectCode() {
        if (initVal instanceof IRConstInt constInt) {
            if (constInt.type() == IRBasicType.I32) {
                // aka. int
                new MIPSWord(name().substring(1), constInt.getValue(), "no message to display");
            } else {
                // aka. char
                new MIPSByte(name().substring(1), constInt.getValue(), "no message to display");
            }
        } else {
            IRConstArray array = (IRConstArray) initVal;
            assert array.getElemType() == IRBasicType.I32 || array.getElemType() == IRBasicType.I8;
            if (array.getElemType() == IRBasicType.I32) {
                // int array
                new MIPSWord(name().substring(1), array.getInitVals(), "no message to display");
            } else {
                // char array
                new MIPSAscii(
                        name.substring(1),
                        array.getInitVals().stream()
                                .map(i -> StringLiteral.display(Character.toString(i)))
                                .collect(Collectors.joining()),
                        "no message to display"
                );
            }
        }
    }

    /**
     * e.g. {@code %1 = global i32 1}
     * <br> {@code %1 = constant [3 x i32] [i32 1, i32 2, i32 0]}
     */
    @Override
    public String toString() {
        String initValStr;
        if (initVal instanceof IRConstInt constInt) {
            initValStr = constInt.name();
        } else {
            initValStr = initVal.toString();
        }
        return String.format("%s = %s %s %s", super.name, (isConst ? "constant" : "global"), objectType, initValStr);
    }
}
