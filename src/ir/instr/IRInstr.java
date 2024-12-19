package ir.instr;

import ir.IRUser;
import ir.IRValue;
import ir.type.IRType;

public abstract class IRInstr extends IRUser {
    protected final IRInstrType instrType;
    public String message = "";

    /**
     * @param valueType return type of the instr.
     * @param name name of the virtual reg.
     * @param instrType type of the instr.
     */
    public IRInstr(IRType valueType, String name, IRInstrType instrType) {
        super(valueType, name);
        this.instrType = instrType;
    }

    public IRInstr(IRType valueType, String name, IRInstrType instrType, String message) {
        super(valueType, name);
        this.instrType = instrType;
        this.message = message;
    }

    public IRInstrType getInstrType() { return this.instrType; }

    public void generateObjectCode() {}

    public String annotate() {
        return String.format("%s [%s]", this, this.message);
    }

    public boolean use(IRValue value) { return this.uses.contains(value); }

    public void replaceUse(IRValue value, IRValue newValue) {}
}
