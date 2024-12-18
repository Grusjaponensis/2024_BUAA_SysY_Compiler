package ir;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.MIPSLabel;
import ir.instr.IRInstr;
import ir.type.IRBasicType;
import ir.type.IRType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IRFunc extends IRValue {
    private final ArrayList<IRValue> params;
    private final ArrayList<IRBasicBlock> basicBlocks = new ArrayList<>();
    private final IRType returnType;
    private final HashMap<IRValue, Reg> regMap = new HashMap<>();

    public IRFunc(String name, IRType returnType, ArrayList<IRValue> params) {
        super(IRBasicType.Function, "@" + name);
        this.returnType = returnType;
        this.params = params;
    }

    public void addBasicBlock(IRBasicBlock block) {
        basicBlocks.add(block);
    }

    public void addInstr(IRInstr instr) {
        basicBlocks.get(basicBlocks.size() - 1).addInstr(instr);
    }

    public ArrayList<IRValue> getParams() { return params; }

    public IRBasicBlock currentBlock() { return basicBlocks.get(basicBlocks.size() - 1); }

    public void generateObjectCode() {
        new MIPSLabel(super.name.substring(1), " ======> " + super.name + " <====== ");
        MIPSBuilder.getInstance().functionPrologue(regMap);
        IntStream.range(0, Math.min(4, params.size())).forEach(i -> {
            regMap.put(params.get(i), Reg.values()[3 + i]);
            MIPSBuilder.getInstance().stackPush(params.get(i), 4);
        });
        basicBlocks.forEach(IRBasicBlock::generateObjectCode);
    }

    public String generateIR(boolean forPrint) {
        StringBuilder b = new StringBuilder();
        b.append("define ").append(returnType).append(" ").append(name).append("(");
        b.append(params.stream().map(IRValue::toString).collect(Collectors.joining(", ")));
        b.append(") {\n");
        String blockString = basicBlocks.stream()
                .map(block -> block.generateIR(forPrint))
                .collect(Collectors.joining("\n"));
        b.append(blockString);
        b.append("}\n\n");
        return b.toString();
    }
}
