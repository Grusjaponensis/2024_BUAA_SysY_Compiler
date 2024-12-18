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
    public int objectSize() {
        if (elementType == IRBasicType.I8) {
            // alignment requires a next multiple of 4
            return (elementNum + 3) & ~3;
        } else {
            // type of I32
            return elementNum << 2;
        }
    }

    @Override
    public String toString() {
        return "[" + elementNum + " x " + elementType + "]";
    }
}
