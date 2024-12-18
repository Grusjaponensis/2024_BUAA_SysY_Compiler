package ir.type;

/**
 * Defines basic types for LLVM IR.
 * <p>
 * Supported types:
 * <ul>
 *     <li>{@code Module}, {@code Function}, {@code BasicBlock}</li>
 *     <li>{@code I32} - 32-bit integer</li>
 *     <li>{@code I8} - 8-bit integer (Char)</li>
 *     <li>{@code I1} - 1-bit integer (Bool)</li>
 *     <li>{@code Void} - void type</li>
 * </ul>
 */
public class IRBasicType extends IRType {
    private static final int INT_SIZE = 4;

    public final static IRBasicType Module = new IRBasicType();
    public final static IRBasicType Function = new IRBasicType();
    public final static IRBasicType BasicBlock = new IRBasicType();

    /// aka. {@code int}
    public final static IRBasicType I32 = new IRBasicType();
    /// aka. {@code char}
    public final static IRBasicType I8 = new IRBasicType();
    /// aka. {@code bool}
    public final static IRBasicType I1 = new IRBasicType();

    public final static IRBasicType Void = new IRBasicType();

    private IRBasicType() {}

    @Override
    public int objectSize() {
        if (this == Module || this == Function || this == BasicBlock || this == Void) {
            throw new UnsupportedOperationException("Illegal object size");
        }
        return INT_SIZE;
    }

    @Override
    public String toString() {
        if (this == I32) {
            return "i32";
        } else if (this == I8) {
            return "i8";
        } else if (this == I1) {
            return "i1";
        } else if (this == Void) {
            return "void";
        } else {
            throw new RuntimeException("Unsupported toString type: " + this);
        }
    }
}
