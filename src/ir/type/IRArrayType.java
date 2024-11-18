package ir.type;

public class IRArrayType extends IRType {
    private final int elementNum;
    private final IRType elementType;

    public IRArrayType(int elementNum, IRType elementType) {
        this.elementNum = elementNum;
        this.elementType = elementType;
    }

    public IRType getElementType() { return elementType; }

    @Override
    public String toString() {
        return "[" + elementNum + " x " + elementType + "]";
    }
}
