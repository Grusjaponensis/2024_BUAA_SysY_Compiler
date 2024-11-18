package ir;

import ir.instr.IRGlobal;
import ir.instr.IRInstr;

/**
 * Virtual register naming helper, auto increment number represents SSA form.
 */
public class IRBuilder {
    private final static IRBuilder instance = new IRBuilder();
    private int localCounter = 0;
    private int basicBlockCounter = 0;

    private final IRModule irModule = new IRModule("testfile");
    private IRFunc currentFunc;

    private IRBuilder() {}

    public static IRBuilder getInstance() { return instance; }

    private void resetLocal() { this.localCounter = 0; }

    private void resetBasicBlock() { this.basicBlockCounter = 0; }

    /// {@code localCounter} will auto increase.
    public String localReg() { return "%" + localCounter++; }

    public String blockReg() { return "bb_" + basicBlockCounter++; }

    private void setCurrentFunc(IRFunc currentFunc) { this.currentFunc = currentFunc; }

    public void addGlobalVar(IRGlobal global) { irModule.addGlobal(global); }

    public void addInstr(IRInstr instr) {
        assert currentFunc != null;
        currentFunc.addInstr(instr);
    }

    public void addFunc(IRFunc func) {
        resetLocal();
        resetBasicBlock();
        irModule.addFunc(func);
        setCurrentFunc(func);
    }

    public String generateIR() {
        return irModule.generateIR();
    }
}
