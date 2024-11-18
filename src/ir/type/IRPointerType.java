package ir.type;

public class IRPointerType extends IRType {
    private final IRType objectType;

    public IRPointerType(IRType objectType) {
        this.objectType = objectType;
    }

    public IRType getObjectType() { return objectType; }

    @Override
    public String toString() {
        return objectType + "*";
    }
}
