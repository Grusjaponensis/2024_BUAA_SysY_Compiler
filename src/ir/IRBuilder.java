package ir;

import ir.instr.IRGlobal;
import ir.instr.IRInstr;

import java.util.Stack;

/**
 * Virtual register naming helper, auto increment number represents SSA form.
 */
public class IRBuilder {
    private final static IRBuilder instance = new IRBuilder();
    private int localCounter = 0;
    private int globalStringCounter = 0;
    private int basicBlockCounter = 0;

    private final IRModule irModule = new IRModule("testfile");
    private IRFunc currentFunc;

    private final Stack<IRBasicBlock> breakForBlockStack = new Stack<>();
    private final Stack<IRBasicBlock> continueBlockStack = new Stack<>();

    private IRBuilder() {}

    public static IRBuilder getInstance() { return instance; }

    private void resetLocal() { this.localCounter = 0; }

    /// {@code localCounter} will auto increase.
    public String localReg() { return "%v" + localCounter++; }

    public String stringReg() { return ".str_" + globalStringCounter++; }

    public String blockReg() { return "bb_" + basicBlockCounter++; }

    public IRBasicBlock currentBlock() {
        assert currentFunc != null;
        return currentFunc.currentBlock();
    }

    private void setCurrentFunc(IRFunc currentFunc) { this.currentFunc = currentFunc; }

    public void addGlobalVar(IRGlobal global) { irModule.addGlobal(global); }

    public void addInstr(IRInstr instr) {
        assert currentFunc != null;
        currentFunc.addInstr(instr);
    }

    public void addBasicBlock(IRBasicBlock block) {
        assert currentFunc != null;
        currentFunc.addBasicBlock(block);
    }

    public void addFunc(IRFunc func) {
        resetLocal();
        irModule.addFunc(func);
        setCurrentFunc(func);
    }

    /// Generating IR for break and continue stmt.
    public void enterLoopWithBlock(IRBasicBlock breakBlock, IRBasicBlock continueBlock) {
        breakForBlockStack.push(breakBlock);
        continueBlockStack.push(continueBlock);
    }

    public IRBasicBlock getBreakForBlock() { return breakForBlockStack.peek(); }

    public IRBasicBlock getContinueBlock() { return continueBlockStack.peek(); }

    public void exitLoop() {
        breakForBlockStack.pop();
        continueBlockStack.pop();
    }

    public void optimize() {
        irModule.optimize();
    }

    public String generateIR(boolean forPrint) {
        return irModule.generateIR(forPrint);
    }

    public void generateObjectCode() { irModule.generateObjectCode(); }
}
