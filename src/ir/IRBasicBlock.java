package ir;

import ir.instr.IRInstr;
import ir.type.IRBasicType;
import util.Debug;

import java.util.ArrayList;

public class IRBasicBlock extends IRValue {
    private final ArrayList<IRInstr> instructions = new ArrayList<>();
    private IRFunc belongsTo;

    public IRBasicBlock(String name) {
        super(IRBasicType.BasicBlock, name);
    }

    public void addInstr(IRInstr instr) {
        instructions.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(name).append(":\n");
        instructions.forEach(
            i -> b.append("  ").append(String.format("%-80s", i)).append(Debug.DEBUG_STATE ? Debug.TERM_ITALIC : "")
                    .append("  ;  ").append(i.message).append(Debug.DEBUG_STATE ? Debug.TERM_RESET : "").append("\n")
        );
        return b.toString();
    }
}
