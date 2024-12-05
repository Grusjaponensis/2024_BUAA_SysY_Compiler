package ir;

import ir.constant.IRString;
import ir.instr.IRGlobal;
import ir.type.IRBasicType;
import util.Debug;

import java.util.ArrayList;

public class IRModule extends IRValue {
    private final ArrayList<IRGlobal> globalDefinitions = new ArrayList<>();
    private final ArrayList<IRFunc> funcDefinitions = new ArrayList<>();

    public IRModule(String name) {
        super(IRBasicType.Module, name);
    }

    public void addGlobal(IRGlobal global) {
        globalDefinitions.add(global);
    }

    public void addFunc(IRFunc func) {
        funcDefinitions.add(func);
    }

    public String generateIR(boolean forPrint) {
        StringBuilder b = new StringBuilder();
        // Choose the appropriate module info
        String moduleInfoToUse = forPrint ? moduleInfoForPrint : moduleInfo;
        b.append(moduleInfoToUse).append("'").append(name).append("'");
        if (forPrint) {
            b.append(Debug.TERM_RESET);
        }
        b.append("\n").append(lib).append("\n");
        // Append global definitions
        globalDefinitions.forEach(globalDef -> b.append(globalDef).append("\n"));
        if (!globalDefinitions.isEmpty()) {
            b.append("\n");
        }
        // Append function definitions
        funcDefinitions.forEach(funcDef -> b.append(funcDef.generateIR(forPrint)));
        return b.toString();
    }

    public void generateObjectCode() {
        globalDefinitions.forEach(IRGlobal::generateObjectCode);
    }


    private static final String lib = """
            declare i32 @getchar()
            declare i32 @getint()
            declare void @putint(i32)
            declare void @putch(i32)
            declare void @putstr(i8*)
            """;

    private static final String moduleInfo = "; ModuleID = " ;
    private static final String moduleInfoForPrint = Debug.TERM_ITALIC + "; ModuleID = " ;
}
