package symbol;

public abstract class Symbol {
    protected final int lineNum;
    protected final String name;

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
}
