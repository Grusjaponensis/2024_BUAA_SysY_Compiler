package frontend.ast.stmt;

import symbol.SymbolTable;

public interface Statement {
    void parse();

    void analyzeSemantic(SymbolTable table);
}
