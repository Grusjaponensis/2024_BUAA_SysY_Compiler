package ir;

import ir.constant.IRString;
import ir.instr.IRGlobal;
import ir.type.IRBasicType;
import util.Debug;

import java.util.ArrayList;

public class IRModule extends IRValue {
    private final ArrayList<IRGlobal> globalDefinitions = new ArrayList<>();
    private final ArrayList<IRFunc> funcDefinitions = new ArrayList<>();
    private final ArrayList<IRString> strings = new ArrayList<>();

    public IRModule(String name) {
        super(IRBasicType.Module, name);
    }

    public void addGlobal(IRGlobal global) {
        globalDefinitions.add(global);
    }

    public void addFunc(IRFunc func) {
        funcDefinitions.add(func);
    }

    public void addString(IRString string) {
        strings.add(string);
    }

    public String generateIR() {
        StringBuilder b = new StringBuilder();
        b.append(moduleInfo).append("'").append(name).append("'")
                .append(Debug.DEBUG_STATE ? Debug.TERM_RESET : "")
                .append("\n").append(lib).append("\n");
        globalDefinitions.forEach(globalDef -> b.append(globalDef).append("\n"));
        if (!globalDefinitions.isEmpty()) {
            b.append("\n");
        }
        funcDefinitions.forEach(funcDef -> b.append(funcDef.generateIR()));
        return b.toString();
    }

    private static final String lib = """
            declare i32 @getchar()
            declare i32 @getint()
            declare void @putint(i32)
            declare void @putch(i32)
            """;

    private static final String moduleInfo = (Debug.DEBUG_STATE ? Debug.TERM_ITALIC : "") + "; ModuleID = " ;
}
