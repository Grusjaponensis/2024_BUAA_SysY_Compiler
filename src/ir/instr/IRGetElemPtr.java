package ir.instr;

import ir.IRValue;
import ir.type.IRArrayType;
import ir.type.IRPointerType;
import ir.type.IRType;

public class IRGetElemPtr extends IRInstr {
    private final IRValue ptr;
    private final IRValue secondOffset;

    /**
     * @param valueType type of the content
     * @param name virtual register to store the result
     * @param ptr from which object to fetch item
     * @param offsetSecond offset
     */
    public IRGetElemPtr(IRType valueType, String name, IRValue ptr, IRValue offsetSecond) {
        super(new IRPointerType(valueType), name, IRInstrType.GetElemPtr);
        this.ptr = ptr;
        this.secondOffset = offsetSecond;
    }

    /**
     * @param valueType type of the content
     * @param name virtual register to store the result
     * @param ptr from which object to fetch item
     * @param offsetSecond offset
     * @param message debug message
     */
    public IRGetElemPtr(IRType valueType, String name, IRValue ptr, IRValue offsetSecond, String message) {
        super(new IRPointerType(valueType), name, IRInstrType.GetElemPtr, message);
        this.ptr = ptr;
        this.secondOffset = offsetSecond;
    }

    /**
     * e.g. {@code %2 = getelemptr inbounds [10 x i32], [10 x i32]* %1, i32 0, i32 %2}
     */
    @Override
    public String toString() {
        IRPointerType pointerType = (IRPointerType) ptr.type();
        if (pointerType.getObjectType() instanceof IRArrayType) {
            return String.format(
                    "%s = getelementptr inbounds %s, %s %s, i32 0, i32 %s",
                    super.name, pointerType.getObjectType(), ptr.type(), ptr.name(), secondOffset.name()
            );
        }
        return String.format(
                "%s = getelementptr inbounds %s, %s %s, i32 %s",
                super.name, pointerType.getObjectType(), ptr.type(), ptr.name(), secondOffset.name()
        );
    }
}
