package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;

public class IRPutCh extends IRInstr {
    private char c;
    private IRValue value;

    public IRPutCh(char c) {
        super(IRBasicType.Void, "", IRInstrType.PutCh);
        this.c = c;
    }

    public IRPutCh(IRValue value) {
        super(IRBasicType.Void, "", IRInstrType.PutCh);
        this.value = value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return String.format("call void @putch(i32 %s)", value.name());
        }
        return String.format("call void @putch(i32 %d)", (int) c);
    }
}
