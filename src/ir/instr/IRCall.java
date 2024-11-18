package ir.instr;

import ir.IRValue;
import ir.type.IRBasicType;
import ir.type.IRType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRCall extends IRInstr {
    private final String funcName;
    private final ArrayList<IRValue> params;

    public IRCall(IRType valueType, String name,
                  String funcName, ArrayList<IRValue> params) {
        super(valueType, name, IRInstrType.Call);
        this.funcName = funcName;
        this.params = params;
    }

    public IRCall(IRType valueType, String name,
                  String funcName, ArrayList<IRValue> params, String message) {
        super(valueType, name, IRInstrType.Call, message);
        this.funcName = funcName;
        this.params = params;
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
