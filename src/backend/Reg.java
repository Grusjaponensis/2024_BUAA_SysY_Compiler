package backend;

public enum Reg {
    zero,

    // Returned Value of a subroutine
    v0, v1,

    // Argument registers
    a0, a1, a2, a3,

    // Temporary Registers
    t0, t1, t2, t3, t4, t5, t6, t7, t8, t9,

    // Saved Registers
    s0, s1, s2, s3, s4, s5, s6, s7,

    // Kernel Reserved registers
    k0, k1,

    // Globals Pointer used for addressing static global variables
    gp,

    // Stack Pointer
    sp,

    // Frame Pointer
    fp,

    // Return Address in a subroutine call
    ra;

    @Override
    public String toString() {
        return String.format("$%s", this);
    }
}
