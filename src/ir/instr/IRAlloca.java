package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSArithmetic;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.type.IRPointerType;
import ir.type.IRType;

/**
 * {@code Alloca} allocates space and returns a pointer.
 */
public class IRAlloca extends IRInstr {
    private final IRType objectType;

    /**
     * @param objectType Object type to allocate. (integer / array of integer)
     */
    public IRAlloca(IRType objectType, String name) {
        // result of alloca is actually a pointer,
        // so pass a pointer to indicate the result of this instr is pointer type.
        super(new IRPointerType(objectType), name, IRInstrType.Alloca);
        this.objectType = objectType;
    }

    /**
     * @param objectType Object type to allocate. (integer / array of integer)
     */
    public IRAlloca(IRType objectType, String name, String message) {
        super(new IRPointerType(objectType), name, IRInstrType.Alloca, message);
        this.objectType = objectType;
    }

    @Override
    public void generateObjectCode() {
        int addr = MIPSBuilder.getInstance().stackAllocation(objectType.objectSize());
        new MIPSArithmetic(MIPSInstrType.Addi, Reg.t8, Reg.sp, addr, "alloca: " + objectType + ", size: " + objectType.objectSize());
        new MIPSMemory(
                MIPSInstrType.Sw,
                Reg.t8, Reg.sp, // dest, base
                MIPSBuilder.getInstance().stackPush(this, 4), // offset
                annotate()
        );
    }

    /**
     * e.g. {@code %1 = alloca i32}
     */
    @Override
    public String toString() {
        return String.format("%s = alloca %s", super.name, objectType);
    }
}
