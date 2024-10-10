package frontend.ast;

import util.Debug;

public record Ident(String name) {
    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return name;
        }
        return "IDENFR " + name + "\n";
    }
}
