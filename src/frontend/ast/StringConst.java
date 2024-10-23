package frontend.ast;

import util.Debug;

import java.util.stream.IntStream;

public record StringConst(String value) {
    public char[] toCharArray() { return value.toCharArray(); }

    public int paramsNum() {
        return (int) IntStream.range(0, value.length() - 1)
                .filter(i -> value.charAt(i) == '%' &&
                        (value.charAt(i + 1) == 'd' || value.charAt(i + 1) == 'c'))
                .count();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        value.chars().forEach(c -> {
            switch (c) {
                case (char) 7 -> builder.append("\\a");
                case '\b' -> builder.append("\\b");
                case '\t' -> builder.append("\\t");
                case '\n' -> builder.append("\\n");
                case (char) 11 -> builder.append("\\v");
                case '\f' -> builder.append("\\f");
                case '\"' -> builder.append("\\\"");
                case '\'' -> builder.append("\\'");
                case '\\' -> builder.append("\\\\");
                case '\0' -> builder.append("\\0");
                default -> builder.append((char) c);
            }
        });
        if (Debug.DEBUG_STATE) {
            return "<StringConst> " + "\"" + builder + "\"";
        }
        return "STRCON \"" + builder + "\"\n";
    }
}
