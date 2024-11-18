package ir.instr;

import ir.IRBuilder;
import ir.IRValue;
import ir.type.IRBasicType;
import ir.type.IRPointerType;
import ir.type.IRType;

public class IRTypeCast extends IRInstr {
    private final IRValue objectToConvert;
    // target type: super.type

    public IRTypeCast(String name, IRInstrType instrType, IRValue object, IRType targetType) {
        super(targetType, name, instrType);
        objectToConvert = object;
    }

    public IRTypeCast(String name, IRInstrType instrType,
                      IRValue object, IRType targetType, String message) {
        super(targetType, name, instrType, message);
        objectToConvert = object;
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

    /**
     * e.g. {@code %2 = trunc i32 %1 to i8}
     */
    @Override
    public String toString() {
        return name + " = " + instrType.toString().toLowerCase()
                + " " + objectToConvert.type() + " " + objectToConvert.name()
                + " to " + super.type;
    }
}
