package ir;

import backend.instr.MIPSLabel;
import ir.constant.IRConstInt;
import ir.instr.IRArithmetic;
import ir.instr.IRInstr;
import ir.type.IRBasicType;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRBasicBlock extends IRValue {
    private final ArrayList<IRInstr> instructions = new ArrayList<>();
    private final ArrayList<IRBasicBlock> parent = new ArrayList<>();

    public IRBasicBlock(String name) {
        super(IRBasicType.BasicBlock, name);
    }

    public void addInstr(IRInstr instr) {
        instructions.add(instr);
    }

    public void addParent(IRBasicBlock parent) { this.parent.add(parent); }

    public void generateObjectCode() {
        new MIPSLabel(super.name, "");
        instructions.forEach(IRInstr::generateObjectCode);
    }

    public void constantFolding() {
        for (int i = 0; i < instructions.size(); i++) {
            IRInstr instr = instructions.get(i);
            if (instr instanceof IRArithmetic arithmetic) {
                if (arithmetic.getOperand1() instanceof IRConstInt const1 &&
                        arithmetic.getOperand2() instanceof IRConstInt const2) {
                    int result;
                    switch (arithmetic.getInstrType()) {
                        case Add -> result = const1.getValue() + const2.getValue();
                        case Sub -> result = const1.getValue() - const2.getValue();
                        case Mul -> result = const1.getValue() * const2.getValue();
                        case SDiv -> result = const1.getValue() / const2.getValue();
                        case SRem -> result = const1.getValue() % const2.getValue();
                        default -> {
                            continue;
                        }
                    }
                    instructions.remove(instr);
                    // search for uses and replace them with new result
                    for (int j = i; j < instructions.size(); j++) {
                        IRInstr instrUse = instructions.get(j);
                        if (instrUse.use(instr)) {
                            instrUse.replaceUse(instr, new IRConstInt(IRBasicType.I32, result));
                        }
                    }
                    constantFolding();
                }
            }
        }
    }

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
