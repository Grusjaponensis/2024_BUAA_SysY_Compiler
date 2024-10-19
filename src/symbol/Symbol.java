package symbol;

public class Symbol {
    protected final int lineNum;
    protected final String name;
    protected SymbolType type;

    public Symbol(int lineNum, String name, SymbolType type) {
        this.lineNum = lineNum;
        this.name = name;
        this.type = type;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }

    public boolean isTypeof(SymbolType type) { return this.type == type; }
}
