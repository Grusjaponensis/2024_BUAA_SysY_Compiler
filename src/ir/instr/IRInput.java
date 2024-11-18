package ir.instr;

import ir.type.IRBasicType;

public class IRInput extends IRInstr {
    public IRInput(String name, IRInstrType instrType) {
        super(IRBasicType.I32, name, instrType);
    }

    public IRInput(String name, IRInstrType instrType, String message) {
        super(IRBasicType.I32, name, instrType, message);
    }

    @Override
    public String toString() {
        return String.format("%s = call i32 @%s()", super.name, instrType.toString().toLowerCase());
    }
}
