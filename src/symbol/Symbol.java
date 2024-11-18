package symbol;

import ir.IRValue;

public abstract class Symbol {
    protected final int lineNum;
    protected final String name;
    protected IRValue irValue;

    public Symbol(int lineNum, String name) {
        this.lineNum = lineNum;
        this.name = name;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getName() {
        return name;
    }

    public IRValue getIrValue() { return irValue; }

    public void setIrValue(IRValue irValue) { this.irValue = irValue; }
}
