package backend.instr;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MIPSWord extends MIPSInstr {
    private final String name;
    private int initVal;
    private ArrayList<Integer> initVals;
    private final boolean isArray;

    public MIPSWord(String name, int initVal, String message) {
        super(MIPSInstrType.Word, true, message);
        this.name = name;
        this.initVal = initVal;
        this.isArray = false;
    }

    public MIPSWord(String name, ArrayList<Integer> initVals, String message) {
        super(MIPSInstrType.Word, true, message);
        this.initVals = new ArrayList<>(initVals);
        this.name = name;
        this.isArray = true;
    }

    @Override
    public String toString() {
        return String.format(
                "%s:%n\t.word    %s",
                name,
                !isArray ? String.valueOf(initVal) : initVals.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))
        );
    }
}
