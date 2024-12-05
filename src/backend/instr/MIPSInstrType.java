package backend.instr;

public enum MIPSInstrType {
    // arithmetic
    Add, Addi, Sub, Mult, Div,

    // memory
    Sw, Lw,

    // syscall
    Syscall,

    // pseudo instr
    Move, Li, La,

    // branch
    J, Jal, Jr, Beq, Bne,

    // allocation
    Word, Byte, Ascii, Label,

    // cmp
    Slt, Sle, Sgt, Sge, Seq, Sne;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
