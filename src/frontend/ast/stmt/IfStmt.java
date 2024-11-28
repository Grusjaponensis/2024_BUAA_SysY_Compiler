package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.CondNode;
import frontend.ast.StmtNode;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBasicBlock;
import ir.IRBuilder;
import ir.instr.IRJump;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code IfStmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]}
 */
public class IfStmt extends ASTNode implements Statement {
    public enum Type {
        HasElse, NoElse
    }
    private Type type;
    private CondNode condition;
    private StmtNode ifStmt;
    private StmtNode elseStmt;

    public IfStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.IfKeyword, "if");
        expect(TokenType.LParenthesis, "(");
        condition = new CondNode(tokens, depth + 1);
        condition.parse();
        expect(TokenType.RParenthesis, ")");
        ifStmt = new StmtNode(tokens, depth + 1);
        ifStmt.parse();

        if (tokens.get().isTypeOf(TokenType.ElseKeyword)) {
            type = Type.HasElse;
            tokens.advance();
            elseStmt = new StmtNode(tokens, depth + 1);
            elseStmt.parse();
        } else {
            type = Type.NoElse;
        }
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        condition.analyzeSemantic(table);
        ifStmt.analyzeSemantic(table);
        if (type == Type.HasElse) {
            elseStmt.analyzeSemantic(table);
        }
    }

    @Override
    public void generateIR(SymbolTable table) {
        // bb contains instructions in if body
        IRBasicBlock ifBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        IRBasicBlock elseBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        // bb contains instructions after if and else body
        IRBasicBlock endIfBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        if (type == Type.HasElse) {
            // if cond is not satisfied, br to else-stmt
            condition.generateIR(table, ifBlock, elseBlock);
            IRBuilder.getInstance().addBasicBlock(ifBlock);
            // all if stmt instr should insert into ifBB
            ifStmt.generateIR(table);
            // jump to endIf directly
            if (!ifStmt.isLastStmtReturn()) {
                IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            }
            // IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            IRBuilder.getInstance().addBasicBlock(elseBlock);
            elseStmt.generateIR(table);
            // jump to endIf directly
            if (!elseStmt.isLastStmtReturn()) {
                IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            }
            // IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            IRBuilder.getInstance().addBasicBlock(endIfBlock);
        } else {
            // if cond is not satisfied, br to endIf-stmt
            condition.generateIR(table, ifBlock, endIfBlock);
            IRBuilder.getInstance().addBasicBlock(ifBlock);
            ifStmt.generateIR(table);
            if (!ifStmt.isLastStmtReturn()) {
                IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            }
            // IRBuilder.getInstance().addInstr(new IRJump(endIfBlock));
            IRBuilder.getInstance().addBasicBlock(endIfBlock);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            String s = "  ".repeat(depth);
            b.append(s).append("<IfStmt>\n").append(condition).append(ifStmt);
            if (type == Type.HasElse) {
                b.append(s).append("<ElseStmt>\n").append(elseStmt);
            }
            return b.toString();
        }
        b.append("IFTK if\n").append("LPARENT (\n").append(condition).append("RPARENT )\n").append(ifStmt);
        if (type == Type.HasElse) {
            b.append("ELSETK else\n").append(elseStmt);
        }
        return b.toString();
    }
}
