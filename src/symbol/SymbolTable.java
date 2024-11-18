package symbol;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private final HashMap<String, Symbol> symbolTable = new HashMap<>();
    private final SymbolTable parent;
    private final int depth;
    private boolean isInLoop = false;

    private boolean isInFuncDefinition = false;
    private ValueType funcReturnType = null;

    // reserved for func call params analysis
    private final Stack<Func> funcToCall = new Stack<>();
    private final Stack<Integer> paramsIndex = new Stack<>();
    private final Stack<Boolean> isInLValBracket = new Stack<>();

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
        // following attributes should inherit from parent table
        this.isInLoop = parent.isInLoop;
        this.depth = parent.depth + 1;
        this.funcReturnType = parent.funcReturnType;
        this.isInFuncDefinition = parent.isInFuncDefinition;

        // for output
        scopeId = globalScopeId + 1;
        globalScopeId++;
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

    /**
     * Check if a symbol exists in the current scope.
     * @param name The symbol name to check.
     * @return {@code true} if the symbol exists, {@code false} otherwise.
     */
    public boolean hasSymbol(String name) {
        return find(name) != null;
    }

    /**
     * Insert a symbol into the current scope. Reports an error if the symbol is redefined.
     * @param symbol The symbol to insert.
     */
    public void insert(Symbol symbol) {
        // only consider symbol in this scope
        if (symbolTable.containsKey(symbol.name)) {
            ErrorCollector.getInstance().addError(
                    new CompileError(symbol.getLineNum(), ErrorType.RedefinedSymbol,
                            "previous definition is " + symbolTable.get(symbol.name))
            );
            return;
        }
        symbolTable.put(symbol.name, symbol);
        // According to output rules, we should output symbols in order
        symbols.add(symbol);
    }

    /**
     * Enter a loop, marking the current context as within a loop.
     */
    public void enterLoop() { isInLoop = true; }

    public void exitLoop() { isInLoop = false; }

    /**
     * Check if the current context is inside a loop.
     * @return {@code true} if inside a loop, {@code false} otherwise.
     */
    public boolean isInLoop() { return isInLoop; }

    public int depth() { return depth; }

    /**
     * Get the function being analyzed for parameter types.
     * @return The current function being analyzed.
     */
    public Func getFuncToCall() { return funcToCall.peek(); }

    /**
     * Set the function to be analyzed for function call parameters.
     * If {@code null} is passed, the current function is removed.
     * @param func The function being called, or {@code null} to clear.
     */
    public void setFuncToCall(Func func) {
        if (func == null) {
            this.funcToCall.pop();
            resetParamsIndex();
            return;
        }
        this.funcToCall.push(func);
        this.paramsIndex.push(0);
    }

    /**
     * @return {@code true} if current func call is inside another function call.
     */
    public boolean isAnalyzeFuncCallParams() { return !this.funcToCall.empty(); }

    public int getParamsIndex() { return paramsIndex.peek(); }

    /**
     * Increment the parameter index for the current function call being analyzed.
     */
    public void incrementParamsIndex() {
        int oldValue = paramsIndex.pop();
        paramsIndex.push(oldValue + 1);
    }

    /**
     * Reset the parameter index after finishing analysis of a function call.
     */
    public void resetParamsIndex() { this.paramsIndex.pop(); }

    /**
     * Record if current block is in function def, for return stmt check
     * @return {@code true} if current block is in func def, {@code false} otherwise
     */
    public boolean inInFuncDef() { return isInFuncDefinition; }

    public ValueType getFuncReturnType() { return funcReturnType; }

    public void setInFuncDef(ValueType type) {
        if (type == null) {
            this.funcReturnType = null;
            this.isInFuncDefinition = false;
            return;
        }
        this.funcReturnType = type;
        this.isInFuncDefinition = true;
    }

    public boolean isInLValBracket() { return !isInLValBracket.empty(); }

    public void enterLValBracket() { isInLValBracket.push(true); }

    public void exitLValBracket() { isInLValBracket.pop(); }

    public void toggleBracketStatus() { isInLValBracket.push(!isInLValBracket.pop()); }

    public boolean getBracketStatus() { return isInLValBracket.peek(); }

    /// For Output
    public static int globalScopeId = 1;
    public int scopeId = 1;
    public final ArrayList<Symbol> symbols = new ArrayList<>();
    public final ArrayList<SymbolTable> childTables = new ArrayList<>();

    public void insertChildTable(SymbolTable child) { this.childTables.add(child); }

    public String output() {
        StringBuilder b = new StringBuilder();
        symbols.forEach(b::append);
        childTables.forEach(child -> b.append(child.output()));
        return b.toString();
    }
}
