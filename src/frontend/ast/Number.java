package frontend.ast;

import util.Debug;

public record Number(int number) {
    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return "<Number> " + number;
        }
        return "INTCON " + number + "\n" + "<Number>\n";
    }
}
