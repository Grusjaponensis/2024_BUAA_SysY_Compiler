package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBasicBlock;
import ir.IRBuilder;
import ir.IRFunc;
import ir.type.IRBasicType;
import symbol.SymbolTable;
import symbol.ValueType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code MainFuncDef -> 'int' 'main' '(' ')' Block}
 */
public class MainFuncDefNode extends ASTNode {
    private BlockNode body;
    private SymbolTable symbolTable;
    private int endLineNum;


    public MainFuncDefNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        expect(TokenType.IntKeyword, "int");
        expect(TokenType.MainKeyword, "main");
        expect(TokenType.LParenthesis, "(");
        expect(TokenType.RParenthesis, ")");
        body = new BlockNode(tokens, depth + 1);
        body.parse();
        endLineNum = tokens.prev().getLineNumber();
    }

    public void analyzeSemantic(SymbolTable table) {
        symbolTable = new SymbolTable(table);
        table.insertChildTable(symbolTable);
        body.analyzeSemantic(symbolTable);
        body.checkReturnStmt(ValueType.Int, endLineNum);
    }

    public void generateIR() {
        IRFunc irFunc = new IRFunc("main", IRBasicType.I32, new ArrayList<>());
        IRBuilder.getInstance().addFunc(irFunc);
        IRBasicBlock newBlock = new IRBasicBlock(IRBuilder.getInstance().blockReg());
        irFunc.addBasicBlock(newBlock);
        symbolTable.setInFuncDef(ValueType.Int);
        body.generateIR(symbolTable);
        symbolTable.setInFuncDef(null);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<MainFuncDef>\n").append(body);
            return b.toString();
        }
        b.append("INTTK int\nMAINTK main\nLPARENT (\nRPARENT )\n").append(body).append("<MainFuncDef>\n");
        return b.toString();
    }
}
