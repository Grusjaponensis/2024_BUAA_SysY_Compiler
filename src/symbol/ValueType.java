package symbol;

import ir.type.IRBasicType;
import ir.type.IRType;

/**
 * {@code Char, Int, Void}
 */
public enum ValueType {
    Char, Int, Void;

    /**
     * <p>
     *     Checks if the current value type matches the given {@code IRType}.
     *     <ul>
     *         <li>{@code Char} <=> {@code I8}</li>
     *         <li>{@code Int} <=> {@code I32}</li>
     *         <li>{@code Void} <=> {@code Void}</li>
     *     </ul>
     * </p>
     * @param type The {@code IRType} to check.
     * @return {@code true} if the types match, {@code false} otherwise.
     */
    public boolean match(IRType type) {
        return switch (this) {
            case Char -> type == IRBasicType.I8;
            case Int -> type == IRBasicType.I32;
            case Void -> type == IRBasicType.Void;
        };
    }

    public IRType mapToIRType() {
        return switch (this) {
            case Char -> IRBasicType.I8;
            case Int -> IRBasicType.I32;
            case Void -> IRBasicType.Void;
        };
    }
}
