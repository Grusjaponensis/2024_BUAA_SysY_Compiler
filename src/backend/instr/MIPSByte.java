package backend.instr;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MIPSByte extends MIPSInstr {
    private final String name;
    private int initVal;
    private ArrayList<Integer> initVals;
    private final boolean isArray;

    public MIPSByte(String name, int initVal, String message) {
        super(MIPSInstrType.Byte, true, message);
        this.name = name;
        this.initVal = initVal;
        this.isArray = false;
    }

    public MIPSByte(String name, ArrayList<Integer> initVals, String message) {
        super(MIPSInstrType.Byte, true, message);
        this.name = name;
        this.initVals = initVals;
        this.isArray = true;
    }

    @Override
    public String toString() {
        return String.format(
                "%s:%n\t.byte    %s",
                name,
                !isArray ? String.valueOf(initVal) : initVals.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))
        );
    }
}
