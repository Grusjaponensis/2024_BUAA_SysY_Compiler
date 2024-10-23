package exception;

import static util.Debug.*;

public class CompileError extends Throwable implements Comparable<CompileError> {
    protected final Integer line;
    protected final ErrorType type;
    protected final String message;

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

    /**
     * Construct a CompileError without message.
     * @param line - In which line this line occurs
     * @param type - Type of this error
     */
    public CompileError(int line, ErrorType type) {
        this.line = line;
        this.type = type;
        this.message = null;
    }

    @Override
    public int compareTo(CompileError o) {
        return this.line.compareTo(o.line);
    }

    public int getLine() { return line; }

    public ErrorType getType() { return type; }

    @Override
    public String toString() {
        if (DEBUG_STATE) {
            return TERM_RED + "error: " + TERM_RESET +
                    TERM_BOLD + "In line " + line + ": " + type.getMessage() +
                    (message != null ? (", " + message) : "") +
                    " [" + type + "]" + TERM_RESET + "\n";
        }
        return line + " " + type.getType() + "\n";
    }
}
