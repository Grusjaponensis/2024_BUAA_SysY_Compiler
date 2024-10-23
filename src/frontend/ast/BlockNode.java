package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.stmt.ReturnStmt;
import frontend.token.TokenList;
import frontend.token.TokenType;
import symbol.SymbolTable;
import symbol.ValueType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code Block -> '{' { BlockItem } '}'}
 */
public class BlockNode extends ASTNode {
    private final ArrayList<BlockItemNode> blockItems = new ArrayList<>();

    public BlockNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        expect(TokenType.LBrace, "{");
        while (!tokens.get().isTypeOf(TokenType.RBrace)) {
            BlockItemNode node = new BlockItemNode(tokens, depth + 1);
            node.parse();
            blockItems.add(node);
        }
        tokens.advance();
    }

    public void analyzeSemantic(SymbolTable table) {
        blockItems.forEach(item -> item.analyzeSemantic(table));
    }

    /**
     * Check if the last stmt of a <strong>non-void</strong> function is a Return Stmt.
     * @param returnType the return type of the function to check.
     * @param endLineNum the line number where <code>}</code> occurs in this block.
     */
    public void checkReturnStmt(ValueType returnType, int endLineNum) {
        if (returnType != ValueType.Void) {
            // check the last stmt
            BlockItemNode blockItem = getLastStmt();
            if (blockItem == null || blockItem.isDecl()) {
                // blockItem is not a statement
                ErrorCollector.getInstance().addError(
                        new CompileError(endLineNum, ErrorType.MissReturn)
                );
            } else {
                // blockItem is a statement
                if (!(blockItem.getStmt() instanceof ReturnStmt)) {
                    // is not a return stmt
                    ErrorCollector.getInstance().addError(
                            new CompileError(endLineNum, ErrorType.MissReturn)
                    );
                }
            }
        }
    }

    /**
     * @return the last block item in this block, {@code null} if this item is not a statement or no item exists.
     */
    private BlockItemNode getLastStmt() {
        if (!blockItems.isEmpty()) {
            if (blockItems.get(blockItems.size() - 1).isDecl()) {
                return null;
            } else {
                return blockItems.get(blockItems.size() - 1);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<Block>\n");
            blockItems.forEach(b::append);
            return b.toString();
        }
        b.append("LBRACE {\n");
        blockItems.forEach(b::append);
        b.append("RBRACE }\n").append("<Block>\n");
        return b.toString();
    }
}
