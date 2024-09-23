package exception;

import static util.Debug.*;

public class CompileError extends Throwable implements Comparable<CompileError> {
    private final Integer line;
    private final ErrorType type;
    private final String message;

    /**
     * @param line In which line this exception occurs
     * @param type Type of this exception
     * @param message Message of this exception
     */
    public CompileError(int line, ErrorType type, String message) {
        this.line = line;
        this.type = type;
        this.message = message;
    }

    @Override
    public int compareTo(CompileError o) {
        return this.line.compareTo(o.line);
    }

    @Override
    public String toString() {
        if (DEBUG_STATE) {
            return TERM_RED + "error: " + TERM_RESET +
                    TERM_BOLD + "In line " + line + ": " +
                    type.getMessage() + " " + message +
                    " [" + type + "]" + TERM_RESET + "\n";
        }
        return line + " " + type.getType() + "\n";
    }
}
