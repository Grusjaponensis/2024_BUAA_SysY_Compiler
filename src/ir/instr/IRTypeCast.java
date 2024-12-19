package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.IRBuilder;
import ir.IRValue;
import ir.type.IRBasicType;
import ir.type.IRPointerType;
import ir.type.IRType;

public class IRTypeCast extends IRInstr {
    private IRValue objectToConvert;
    // target type: super.type

    public IRTypeCast(String name, IRInstrType instrType, IRValue object, IRType targetType) {
        super(targetType, name, instrType);
        objectToConvert = object;
        this.uses.add(objectToConvert);
    }

    public IRTypeCast(String name, IRInstrType instrType,
                      IRValue object, IRType targetType, String message) {
        super(targetType, name, instrType, message);
        objectToConvert = object;
        this.uses.add(objectToConvert);
    }

    /**
     * Convert a value to a type corresponding to a pointer's object type. (e.g. {@code alloca} / {@code getElemPtr} / ...)
     * @param object value to convert
     * @param target target value (<strong>must be a pointer</strong>)
     * @return the new v-reg after cast
     */
    public static IRValue typeCast(IRValue object, IRValue target) {
        IRInstr afterCast = new IRTypeCast(
                IRBuilder.getInstance().localReg(),
                object.type() == IRBasicType.I32 ? IRInstrType.Trunc : IRInstrType.Zext,
                object,
                // Notice the target is always a pointer type (result of alloca / elemPtr)
                ((IRPointerType) target.type()).getObjectType()
        );
        IRBuilder.getInstance().addInstr(afterCast);
        return afterCast;
    }

    /**
     * Cast a value to specified type.
     * @param object value to convert.
     * @param targetType target cast type.
     * @return the new v-reg after cast
     */
    public static IRValue typeCast(IRValue object, IRType targetType) {
        if (object.type() != targetType) {
            IRInstr afterCast = new IRTypeCast(
                    IRBuilder.getInstance().localReg(),
                    object.type() == IRBasicType.I32 ? IRInstrType.Trunc : IRInstrType.Zext,
                    object,
                    targetType
            );
            IRBuilder.getInstance().addInstr(afterCast);
            return afterCast;
        } else {
            return object;
        }
    }

    @Override
    public void generateObjectCode() {
        Reg src = MIPSBuilder.getInstance().prepareRegForOperand(this.objectToConvert, Reg.t9);
        if (super.type == IRBasicType.I8) {
            new MIPSMemory(
                    MIPSInstrType.Sb,
                    src, Reg.sp,
                    MIPSBuilder.getInstance().stackPush(this, 4),
                    annotate()
            );
        } else {
            new MIPSMemory(
                    MIPSInstrType.Sw,
                    src, Reg.sp,
                    MIPSBuilder.getInstance().stackPush(this, 4),
                    annotate()
            );
        }
    }

    @Override
    public void replaceUse(IRValue value, IRValue newValue) {
        if (this.objectToConvert == value) {
            this.objectToConvert = newValue;
        }
        this.uses.replaceAll(oldValue -> oldValue == value ? newValue : oldValue);
    }

    /**
     * e.g. {@code %2 = trunc i32 %1 to i8}
     */
    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s to %s",
                name,
                instrType.toString().toLowerCase(),
                objectToConvert.type(),
                objectToConvert.name(),
                super.type
        );
    }
}
