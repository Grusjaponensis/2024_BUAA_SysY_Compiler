package ir.instr;

import ir.type.IRPointerType;
import ir.type.IRType;

/**
 * {@code Alloca} allocates space and returns a pointer.
 */
public class IRAlloca extends IRInstr {
    private final IRType objectType;

    /**
     * @param objectType Object type to alloca. (integer / array of integer)
     */
    public IRAlloca(IRType objectType, String name) {
        // result of alloca is actually a pointer,
        // so pass a pointer to indicate the result of this instr is pointer type.
        super(new IRPointerType(objectType), name, IRInstrType.Alloca);
        this.objectType = objectType;
    }

    /**
     * @param objectType Object type to alloca. (integer / array of integer)
     */
    public IRAlloca(IRType objectType, String name, String message) {
        super(new IRPointerType(objectType), name, IRInstrType.Alloca, message);
        this.objectType = objectType;
    }

    /**
     * e.g. {@code %1 = alloca i32}
     */
    @Override
    public String toString() {
        return String.format("%s = alloca %s", super.name, objectType);
    }
}
