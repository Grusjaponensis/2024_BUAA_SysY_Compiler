package exception;

public enum ErrorType {
    IllegalSymbol('a', "Illegal symbol"),
    RedefinedSymbol('b', "Redefined symbol"),
    UndefinedSymbol('c', "Undefined symbol"),
    ParamNumMismatch('d', "Function parameter number mismatch"),
    ParamTypeMismatch('e', "Function parameter type mismatch"),
    VoidReturnValue('f', "Void function has an invalid return statement"),
    MissReturn('g', "Missing return statement in a function with a return value"),
    ChangeConstValue('h', "Cannot change the value of a constant"),
    MissSemicn('i', "Missing semicolon"),
    MissRparent('j', "Missing right parenthesis"),
    MissRbrack('k', "Missing right bracket"),
    FormStringMismatch('l', "Format string mismatch in printf (number of format specifiers and expressions)"),
    NonLoopStmt('m', "Use of break or continue statement outside a loop");

    private final char type;

    private final String message;

    ErrorType(char type, String message) {
        this.type = type;
        this.message = message;
    }

    public char getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
