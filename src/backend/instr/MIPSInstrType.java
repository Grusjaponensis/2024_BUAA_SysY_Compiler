package backend.instr;

public enum MIPSInstrType {
    // arithmetic
    Add, Addu, Addi, Addiu, Sub, Mult, Div, Sll,

    // HI / LO register
    Mfhi, Mflo,

    // memory
    Sw, Lw, Sb, Lb,

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
