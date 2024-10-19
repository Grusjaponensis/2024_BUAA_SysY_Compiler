package exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ErrorCollector {
    private static final ErrorCollector instance = new ErrorCollector();
    private final ArrayList<CompileError> errors = new ArrayList<>();
    private final HashSet<CompileError> errorsSet = new HashSet<>();

    public static ErrorCollector getInstance() { return instance; }

    /**
     * Adds a compile error to the collector.
     *
     * @param error the compile error to be added
     */
    public void addError(CompileError error) {
        if (!errorsSet.contains(error)) {
            errors.add(error);
            errorsSet.add(error);
        }
    }

    /**
     * Checks if there are any errors been collected.
     *
     * @return {@code true} if there are errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Collections.sort(errors);
        errors.forEach(builder::append);
        return builder.toString();
    }
}
