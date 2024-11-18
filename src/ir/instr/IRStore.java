package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;

public class IRStore extends IRInstr {
    private final IRValue src;
    private final IRValue dst;

    public IRStore(IRValue src, IRValue dst) {
        // store don't have a name because it doesn't have a return value
        super(IRBasicType.Void, "", IRInstrType.Store);
        this.src = src;
        this.dst = dst;
    }

    public IRStore(IRValue src, IRValue dst, String message) {
        super(IRBasicType.Void, "", IRInstrType.Store, message);
        this.src = src;
        this.dst = dst;
    }

    /**
     * e.g. {@code store i32 %1, i32* %2}
     */
    @Override
    public String toString() {
        return "store " + src.type() + " " + src.name() + ", " + dst.type() + " " + dst.name();
    }
}
