package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.BlockNode;
import frontend.token.TokenList;
import symbol.SymbolTable;

/**
 * {@code BlockStmt -> Block}
 */
public class BlockStmt extends ASTNode implements Statement {
    private BlockNode block;
    private SymbolTable symbolTable;

    public BlockStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        block = new BlockNode(tokens, depth + 1);
        block.parse();
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        symbolTable = new SymbolTable(table);
        table.insertChildTable(symbolTable);
        block.analyzeSemantic(symbolTable);
    }

    @Override
    public void generateIR(SymbolTable table) {
        block.generateIR(symbolTable);
    }

    public BlockNode getBlock() { return block; }

    @Override
    public String toString() {
        return String.valueOf(block);
    }
}
