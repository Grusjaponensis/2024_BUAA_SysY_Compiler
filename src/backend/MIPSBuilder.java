package backend;

import backend.instr.*;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.instr.IRGlobal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.IntStream;

public class MIPSBuilder {
    private static final MIPSBuilder instance = new MIPSBuilder();
    private final ArrayList<MIPSInstr> dataInstr = new ArrayList<>();
    private final ArrayList<MIPSInstr> textInstr = new ArrayList<>();
    /// According to MIPS Calling Convention, stack is growing in a downward direction, i.e. {@code $sp}
    /// of the current frame should less than its caller's frame.
    private int stackPointerOffset = 0;
    public HashMap<IRValue, Integer> stackOffsetMap = new HashMap<>();
    private HashMap<IRValue, Reg> regMap = new HashMap<>();

    private MIPSBuilder() {}

    public static MIPSBuilder getInstance() { return instance; }

    public void addDataInstr(MIPSInstr instr) {
        dataInstr.add(instr);
    }

    public void addTextInstr(MIPSInstr instr) {
        textInstr.add(instr);
    }

    /**
     * Get the stack offset of specified IRValue. Notice that using {@code Integer} instead of {@code int} is to allow passing {@code null}.
     * @return the stack offset of given IRValue, {@code null} if not mapped in stack.
     */
    public Integer stackOffsetOf(IRValue value) {
        return stackOffsetMap.get(value);
    }

    /**
     * Allocate space on stack and return the offset relative to {@code $sp} after allocation.
     * @param objSize object size to allocate.
     * @return The offset relative to {@code $sp} pointer.
     */
    public int stackAllocation(int objSize) {
        // stack is growing in a downward direction
        stackPointerOffset -= objSize;
        return stackPointerOffset;
    }

    public int stackPush(IRValue value, int objSize) {
        if (stackOffsetMap.get(value) != null) {
            return stackOffsetMap.get(value);
        }
        int offset = stackAllocation(objSize);
        stackOffsetMap.put(value, offset);
        return offset;
    }

    public Reg prepareRegForOperand(IRValue operator, Reg register) {
        if (operator instanceof IRConstInt constInt) {
            // load immediate before using int / char literal in arithmetic operations
            new MIPSLoadImm(register, constInt.getValue(), "load imm: " + constInt);
            return register;
        }
        // If this operand's IRValue is mapped in the stack, load it;
        // otherwise, allocate space for it.
        Integer stackOffset = stackOffsetOf(operator);
        if (stackOffset == null) {
            stackOffset = stackPush(operator, 4);
        }
        new MIPSMemory(MIPSInstrType.Lw, register, Reg.sp, stackOffset, operator.toString());
        return register;
    }

    public Reg prepareRegForPointer(IRValue pointer, Reg register) {
        if (pointer instanceof IRGlobal global) {
            new MIPSLoadAddr(register, pointer.name().substring(1), "load addr: " + global.message);
            return register;
        }
        Integer stackOffset = stackOffsetOf(pointer);
        if (stackOffset == null) {
            stackOffset = stackPush(pointer, 4);
        }
        new MIPSMemory(MIPSInstrType.Lw, register, Reg.sp, stackOffset, pointer.toString());
        return register;
    }

    public void functionPrologue(HashMap<IRValue, Reg> parameterMap) {
        // reset stack pointer
        stackPointerOffset = 0;
        stackOffsetMap = new HashMap<>();
        // prepare parameters
        this.regMap = parameterMap;
    }

    public HashSet<Reg> getMappedRegs() {
        return new HashSet<>(regMap.values());
    }

    public int getStackPointerOffset() { return stackPointerOffset; }

    public String generateObjectCode() {
        StringBuilder code = new StringBuilder();
        // for better readability, sort with the order "word > byte > ascii"
        Collections.sort(dataInstr);
        code.append("# ====================> data segment <====================\n").append(".data\n");
        dataInstr.forEach(instr -> code.append(instr).append("\n\n"));

        // preprocess instructions to eliminate adjacent type J instr
        ArrayList<MIPSInstr> toRemove = new ArrayList<>();
        IntStream.range(1, textInstr.size()).forEach(i -> {
            if (textInstr.get(i).getType() == MIPSInstrType.J && textInstr.get(i - 1).getType() == MIPSInstrType.J) {
                toRemove.add(textInstr.get(i));
            }
        });
        toRemove.forEach(textInstr::remove);

        code.append("# ====================> text segment <====================\n").append(".text\n");

        textInstr.forEach(instr -> {
            if (instr instanceof MIPSLabel) {
                // add additional '\n' before label
                code.append(String.format("%n%-40s", instr))
                        .append("#  ")
                        .append(instr.message)
                        .append("\n");
            } else {
                code.append("\t").append(String.format("%-36s", instr))
                        .append("#  ")
                        .append(instr.message)
                        .append("\n");
            }
        });
        return code.toString();
    }
}
