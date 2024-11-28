package ir;

import ir.instr.IRInstr;
import ir.type.IRBasicType;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRBasicBlock extends IRValue {
    private final ArrayList<IRInstr> instructions = new ArrayList<>();
    private IRFunc belongsTo;
    private final ArrayList<IRBasicBlock> parent = new ArrayList<>();

    public IRBasicBlock(String name) {
        super(IRBasicType.BasicBlock, name);
    }

    public IRBasicBlock(String name, IRBasicBlock parent) {
        super(IRBasicType.BasicBlock, name);
        this.parent.add(parent);
    }

    public void addInstr(IRInstr instr) {
        instructions.add(instr);
    }

    public void addParent(IRBasicBlock parent) { this.parent.add(parent); }

    public String generateIR(boolean forPrint) {
        StringBuilder b = new StringBuilder();
        b.append(String.format("%-82s", name + ":"));
        if (forPrint) {
            b.append(Debug.TERM_ITALIC).append("  ;  preds: ")
                    .append(parent.stream().map(IRBasicBlock::name).collect(Collectors.joining(", ")))
                    .append(Debug.TERM_RESET)
                    .append("\n");
        } else {
            b.append("  ;  preds: ")
                    .append(parent.stream().map(IRBasicBlock::name).collect(Collectors.joining(", ")))
                    .append("\n");
        }
        instructions.forEach(i -> {
            if (forPrint) {
                b.append("  ")
                        .append(String.format("%-80s", i))
                        .append(Debug.TERM_ITALIC)
                        .append("  ;  ")
                        .append(i.message)
                        .append(Debug.TERM_RESET)
                        .append("\n");
            } else {
                b.append("  ")
                        .append(String.format("%-80s", i))
                        .append("  ;  ")
                        .append(i.message)
                        .append("\n");
            }
        });
        return b.toString();
    }
}
