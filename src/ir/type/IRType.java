package ir.type;

/**
 * Base IR Type.
 */
public abstract class IRType {
    /**
     * Calculates the aligned size of the object.
     *
     * @return the size of the object after alignment to the required boundary.
     */
    public int objectSize() { return 0; }
}