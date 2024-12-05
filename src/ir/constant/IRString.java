package ir.constant;

import frontend.token.StringLiteral;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IRString {
    private final String name;
    private final String value;

    public IRString(String name, ArrayList<Integer> splitASCIIs) {
        this.name = name;
        this.value = splitASCIIs.stream()
                .map(i -> StringLiteral.display(Character.toString(i)))
                .collect(Collectors.joining());
    }

    public String getName() { return name; }

    public String getValue() { return value; }
}
