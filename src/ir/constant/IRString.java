package ir.constant;

public class IRString {
    private final String name;
    private final String value = "";

    public IRString(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public String getValue() { return value; }
}
