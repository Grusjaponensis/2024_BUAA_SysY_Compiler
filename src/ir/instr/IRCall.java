package ir.instr;

import backend.MIPSBuilder;
import backend.Reg;
import backend.instr.*;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.type.IRBasicType;
import ir.type.IRType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class IRCall extends IRInstr {
    private final String funcName;
    private final ArrayList<IRValue> params;

    public IRCall(IRType valueType, String name,
                  String funcName, ArrayList<IRValue> params) {
        super(valueType, name, IRInstrType.Call);
        this.funcName = funcName;
        this.params = params;
        this.uses.addAll(params);
    }

    public IRCall(IRType valueType, String name,
                  String funcName, ArrayList<IRValue> params, String message) {
        super(valueType, name, IRInstrType.Call, message);
        this.funcName = funcName;
        this.params = params;
        this.uses.addAll(params);
    }

    @Override
    public void generateObjectCode() {
        HashSet<Reg> mappedRegs = MIPSBuilder.getInstance().getMappedRegs();
        HashMap<Reg, Integer> offsetMap = new HashMap<>();
        mappedRegs.forEach(reg -> {
            int offset = MIPSBuilder.getInstance().stackAllocation(4);
            new MIPSMemory(MIPSInstrType.Sw, reg, Reg.sp, offset, annotate());
            offsetMap.put(reg, offset);
        });
        // In prologue, we should save $ra and $sp to stack
        new MIPSMemory(
                MIPSInstrType.Sw,
                Reg.sp, Reg.sp,
                MIPSBuilder.getInstance().stackAllocation(4),
                "PROLOGUE: save $sp"
        );
        new MIPSMemory(
                MIPSInstrType.Sw,
                Reg.ra, Reg.sp,
                MIPSBuilder.getInstance().stackAllocation(4),
                "PROLOGUE: save $ra"
        );
        // record current stack pointer address for recovery in epilogue
        int stackOffset = MIPSBuilder.getInstance().getStackPointerOffset();

        params.forEach(param -> {
            if (param instanceof IRConstInt constInt) {
                new MIPSLoadImm(Reg.t8, constInt.getValue(), annotate());
                new MIPSMemory(MIPSInstrType.Sw, Reg.t8, Reg.sp, MIPSBuilder.getInstance().stackAllocation(4), "sw: param " + constInt);
            } else {
                Integer offset = MIPSBuilder.getInstance().stackOffsetOf(param);
                if (offset == null) {
                    MIPSBuilder.getInstance().stackPush(param, 4);
                } else {
                    Reg reg = MIPSBuilder.getInstance().prepareRegForOperand(param, Reg.t8);
                    new MIPSMemory(MIPSInstrType.Sw, reg, Reg.sp, MIPSBuilder.getInstance().stackAllocation(4), "sw: param " + param);
                }
            }
        });
        new MIPSArithmetic(MIPSInstrType.Addiu, Reg.sp, Reg.sp, stackOffset, "");
        new MIPSJump(MIPSInstrType.Jal, funcName.substring(1), annotate());

        // In epilogue, restore $sp and $ra
        new MIPSMemory(MIPSInstrType.Lw, Reg.ra, Reg.sp, 0, annotate());
        new MIPSMemory(MIPSInstrType.Lw, Reg.sp, Reg.sp, 4, annotate());
        // restore used registers
        mappedRegs.forEach(reg -> new MIPSMemory(MIPSInstrType.Lw, reg, Reg.sp, offsetMap.get(reg), "EPILOGUE: restore register"));
        // return value
        new MIPSMemory(MIPSInstrType.Sw, Reg.v0, Reg.sp, MIPSBuilder.getInstance().stackPush(this, 4), "sw: return value");
    }

    @Override
    public void replaceUse(IRValue value, IRValue newValue) {
        this.params.replaceAll(param -> param == value ? newValue : param);
        this.uses.replaceAll(u -> u == value ? newValue : u);
    }

    /**
     * e.g. {@code %1 = call i32 @f(i32 10, i32* a)}<br>
     * {@code call void @g()}
     */
    @Override
    public String toString() {
        String prefix = (type == IRBasicType.Void) ? "call void" : (name + " = call " + type);
        String paramStr = params.stream()
                .map(o -> o.type() + " " + o.name())
                .collect(Collectors.joining(", "));
        return String.format("%s %s(%s)", prefix, funcName, paramStr);
    }
}
