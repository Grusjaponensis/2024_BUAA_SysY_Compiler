package frontend.ast.stmt;

import frontend.ast.ASTNode;
import frontend.ast.CondNode;
import frontend.ast.ForStmtNode;
import frontend.ast.StmtNode;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBasicBlock;
import ir.IRBuilder;
import ir.instr.IRJump;
import symbol.SymbolTable;
import util.Debug;

/**
 * {@code ForStmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt}
 */
public class ForStmt extends ASTNode implements Statement {
    private ForStmtNode stmt1;
    private CondNode cond;
    private ForStmtNode stmt2;
    private StmtNode body;

    public ForStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.ForKeyword, "for");
        expect(TokenType.LParenthesis, "(");
        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.Semicolon)) {
            stmt1 = new ForStmtNode(tokens, depth + 1);
            stmt1.parse();
        }

        expect(TokenType.Semicolon, ";");

        token = tokens.get();
        if (!token.isTypeOf(TokenType.Semicolon)) {
            cond = new CondNode(tokens, depth + 1);
            cond.parse();
        }

        expect(TokenType.Semicolon, ";");

        token = tokens.get();
        if (!token.isTypeOf(TokenType.RParenthesis)) {
            stmt2 = new ForStmtNode(tokens, depth + 1);
            stmt2.parse();
        }
        expect(TokenType.RParenthesis, ")");

        body = new StmtNode(tokens, depth + 1);
        body.parse();
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        // for loop cannot declare new variables
        if (stmt1 != null) {
            stmt1.analyzeSemantic(table);
        }
        if (stmt2 != null) {
            stmt2.analyzeSemantic(table);
        }
        if (cond != null) {
            cond.analyzeSemantic(table);
        }
        // mark enter loop
        table.enterLoop();
        body.analyzeSemantic(table);
        // mark outside loop
        table.exitLoop();
    }

    @Override
    public void generateIR(SymbolTable table) {
        IRBasicBlock condBlock, loopBodyBlock, secondStmtBlock, endForBlock;
        condBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        loopBodyBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        secondStmtBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        endForBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        // record in table
        IRBuilder.getInstance().enterLoopWithBlock(
                endForBlock,
                stmt2 != null ? secondStmtBlock : cond != null ? condBlock : loopBodyBlock
        );
        if (stmt1 != null) {
            // first stmt belongs to previous basic block before for loop
            stmt1.generateIR(table);
        }
        if (cond != null) {
            // if cond stmt exists, jump to cond bb, else jump to loop body
            IRBuilder.getInstance().addInstr(new IRJump(condBlock));
            IRBuilder.getInstance().addBasicBlock(condBlock);

            // for condition
            cond.generateIR(table, loopBodyBlock, endForBlock);
        } else {
            IRBuilder.getInstance().addInstr(new IRJump(loopBodyBlock));
        }

        // loop body
        IRBuilder.getInstance().addBasicBlock(loopBodyBlock);
        body.generateIR(table);

        if (stmt2 != null) {
            // if stmt2 is not null, jump to stmt2 bb
            IRBuilder.getInstance().addInstr(new IRJump(secondStmtBlock));
            IRBuilder.getInstance().addBasicBlock(secondStmtBlock);

            stmt2.generateIR(table);
        }
        // after body and stmt2, if cond is not null, jump to cond, else jump to body
        if (cond != null) {
            IRBuilder.getInstance().addInstr(new IRJump(condBlock));
        } else {
            IRBuilder.getInstance().addInstr(new IRJump(loopBodyBlock));
        }

        IRBuilder.getInstance().addBasicBlock(endForBlock);
        IRBuilder.getInstance().exitLoop();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<ForStmt>\n")
                    .append(stmt1 == null ? "" : stmt1)
                    .append(cond == null ? "" : cond)
                    .append(stmt2 == null ? "" : stmt2)
                    .append(body);
            return b.toString();
        }
        b.append("FORTK for\n").append("LPARENT (\n")
                .append(stmt1 == null ? "" : stmt1).append("SEMICN ;\n")
                .append(cond == null ? "" : cond).append("SEMICN ;\n")
                .append(stmt2 == null ? "" : stmt2).append("RPARENT )\n")
                .append(body);
        return b.toString();
    }
}
