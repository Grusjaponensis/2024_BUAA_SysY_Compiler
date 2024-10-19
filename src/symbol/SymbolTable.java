package symbol;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Symbol> symbolTable = new HashMap<>();
    private final SymbolTable parent;
    private final int depth;

    /**
     * Creates a new symbol table with the specified depth.
     *
     * @param depth The depth level of this symbol table, indicating its
     *              position within the scope hierarchy.
     */
    public SymbolTable(int depth) {
        this.depth = depth;
        this.parent = null;
    }

    /**
     * Constructs a new embedded symbol table as a child of the specified parent symbol table.
     *
     * @param parent The parent symbol table from which this embedded table will inherit its
     *               scope context.
     */
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.depth = parent.depth + 1;
    }

    /**
     * Searches for a symbol with the specified name in the current symbol table
     * and its parent tables recursively.
     *
     * @param name The name of the symbol to find.
     * @return The symbol if found; otherwise, returns {@code null}.
     */
    public Symbol find(String name) {
        // Check for the symbol in the current level of the symbol table
        if (symbolTable.containsKey(name)) {
            return symbolTable.get(name);
        }
        // If not found in current scope, check in the parent symbol table recursively
        if (parent != null) {
            return parent.find(name);
        }
        // Symbol is not found in the current scope or any parent scopes
        return null;
    }

    public boolean hasSymbol(String name) {
        return find(name) != null;
    }

    public void insert(Symbol symbol) {
        // only consider
        if (symbolTable.containsKey(symbol.name)) {
            // TODO: error handling
            return;
        }
        symbolTable.put(symbol.name, symbol);
    }

    public int depth() { return depth; }
}
