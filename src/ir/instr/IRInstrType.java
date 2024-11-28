package ir.instr;

public enum IRInstrType {
    // memory
    Alloca, Store, Load, GetElemPtr,

    // arithmetic
    Add, Sub, Mul,
    SDiv,   // Division /
    SRem,   // Mod %

    // branch
    Jump, Branch,

    // logical operation
    Eq, Ne, Sgt, Sge, Slt, Sle,

    // func call
    Call, Ret,

    // I/O
    GetInt, GetChar, PutInt, PutCh,

    // type cast
    Zext, Trunc,

    // special
    Global
}
