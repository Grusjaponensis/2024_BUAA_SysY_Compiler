package ir;

import ir.instr.IRInstr;
import ir.type.IRBasicType;
import ir.type.IRType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRFunc extends IRValue {
    private final ArrayList<IRValue> params;
    private final ArrayList<IRBasicBlock> basicBlocks = new ArrayList<>();
    private final IRType returnType;

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

    public String generateIR() {
        StringBuilder b = new StringBuilder();
        b.append("define ").append(returnType).append(" ").append(name).append("(");
        b.append(params.stream().map(IRValue::toString).collect(Collectors.joining(", ")));
        b.append(") {\n");
        basicBlocks.forEach(b::append);
        b.append("}\n\n");
        return b.toString();
    }
}
