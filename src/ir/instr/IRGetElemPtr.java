package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSArithmetic;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.IRValue;
import ir.type.IRArrayType;
import ir.type.IRBasicType;
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

    @Override
    public void generateObjectCode() {
        Reg dest = Reg.t8;
        Reg pointer = MIPSBuilder.getInstance().prepareRegForPointer(ptr, Reg.t8);
        Reg offset = MIPSBuilder.getInstance().prepareRegForOperand(secondOffset, Reg.t9);
        IRPointerType pointerType = (IRPointerType) ptr.type();
        int shift = getAddrShift(pointerType);
        new MIPSArithmetic(MIPSInstrType.Sll, Reg.t9, offset, shift, "left shift for offset: " + offset);
        new MIPSArithmetic(MIPSInstrType.Addu, dest, Reg.t9, pointer, annotate());
        new MIPSMemory(
                MIPSInstrType.Sw,
                dest, Reg.sp,
                MIPSBuilder.getInstance().stackPush(this, 4),
                annotate()
        );
    }

    private int getAddrShift(IRPointerType pointerType) {
        int shift;
        if (pointerType.getObjectType() instanceof IRArrayType arrayType) {
            IRType elementType = arrayType.getElementType();
            if (elementType == IRBasicType.I8) {
                shift = 0;
            } else {
                shift = 2;
            }
        } else {
            // instanceof pointerType
            if (pointerType.getObjectType() == IRBasicType.I8) {
                shift = 0;
            } else {
                shift = 2;
            }
        }
        return shift;
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
