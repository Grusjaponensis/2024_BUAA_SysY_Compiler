package backend;

import backend.instr.MIPSInstr;

import java.util.ArrayList;
import java.util.Collections;

public class MIPSBuilder {
    private static final MIPSBuilder instance = new MIPSBuilder();
    private final ArrayList<MIPSInstr> dataInstr = new ArrayList<>();
    private final ArrayList<MIPSInstr> textInstr = new ArrayList<>();

    private MIPSBuilder() {}

    public static MIPSBuilder getInstance() { return instance; }

    public void addDataInstr(MIPSInstr instr) {
        dataInstr.add(instr);
    }

    public void addTextInstr(MIPSInstr instr) {
        textInstr.add(instr);
    }

    public String generateObjectCode() {
        StringBuilder code = new StringBuilder();
        // for better readability, sort with the order "word > byte > ascii"
        Collections.sort(dataInstr);
        code.append("# ====================> data segment <====================\n").append(".data\n");
        dataInstr.forEach(instr -> code.append(instr).append("\n\n"));

        code.append("# ====================> text segment <====================\n").append(".text\n");
        textInstr.forEach(instr -> code.append(String.format("%-60s", instr))
                .append("#  ")
                .append(instr.message)
                .append("\n")
        );
        return code.toString();
    }
}
