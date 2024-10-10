package frontend.ast;

import util.Debug;

public record Char(char c) {
    @Override
    public String toString() {
        String s;
        switch (c) {
            case (char) 7 -> s = "\\a";
            case '\b' -> s = "\\b";
            case '\t' -> s = "\\t";
            case '\n' -> s = "\\n";
            case (char) 11 -> s = "\\v";
            case '\f' -> s = "\\f";
            case '\"' -> s = "\\\"";
            case '\'' -> s = "\\'";
            case '\\' -> s = "\\\\";
            case '\0' -> s = "\\0";
            default -> s = String.valueOf(c);
        }
        if (Debug.DEBUG_STATE) {
            return "<Character> " + "'" + s + "'";
        }
        return "CHRCON " + "'" + s + "'" + "\n" + "<Character>\n";
    }
}
