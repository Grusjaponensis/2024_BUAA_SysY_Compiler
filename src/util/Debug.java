package util;

public class Debug {
    public static final boolean DEBUG_STATE = true;

    public static final boolean STAGE_LEXER = false;
    public static final boolean STAGE_PARSER = true;

    public static final String TERM_RESET = "\u001B[0m";
    public static final String TERM_RED = "\u001B[1;31m";
    public static final String TERM_GREEN = "\u001B[1;32m";
    public static final String TERM_YELLOW = "\u001B[1;33m";
    public static final String TERM_BLUE = "\u001B[1;34m";
    public static final String TERM_MAGENTA = "\u001B[1;35m";
    public static final String TERM_CYAN = "\u001B[1;36m";
    public static final String TERM_BOLD = "\u001B[1m";


    public static void log(String message) {
        if (DEBUG_STATE) {
            System.out.println(TERM_MAGENTA + message + TERM_RESET);
        }
    }
}
