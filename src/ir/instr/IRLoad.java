package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSInstrType;
import backend.instr.MIPSMemory;
import ir.IRValue;
import ir.type.IRBasicType;
import ir.type.IRPointerType;

public class IRLoad extends IRInstr {
    private final IRValue loadFrom;

    public IRLoad(String name, IRValue loadFrom) {
        // auto dereference
        super(((IRPointerType) loadFrom.type()).getObjectType(), name, IRInstrType.Load);
        this.loadFrom = loadFrom;
    }

    public IRLoad(String name, IRValue loadFrom, String message) {
        super(((IRPointerType) loadFrom.type()).getObjectType(), name, IRInstrType.Load, message);
        this.loadFrom = loadFrom;
    }

    @Override
    public void generateObjectCode() {
        Reg pointerReg = MIPSBuilder.getInstance().prepareRegForPointer(loadFrom, Reg.t8);
        Reg dest = Reg.t8;
        if (loadFrom.type() instanceof IRPointerType pointerType) {
            if (pointerType.getObjectType() == IRBasicType.I8) {
                // char array, should use lb to fetch items
                new MIPSMemory(MIPSInstrType.Lb, dest, pointerReg, 0, annotate());
            } else {
                // int array
                new MIPSMemory(MIPSInstrType.Lw, dest, pointerReg, 0, annotate());
            }
        }
        new MIPSMemory(
                MIPSInstrType.Sw,
                Reg.t8, Reg.sp,
                MIPSBuilder.getInstance().stackPush(this, 4),
                annotate()
        );
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", super.name, super.type, loadFrom.type(), loadFrom.name());
    }
}
