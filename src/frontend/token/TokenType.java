package frontend.token;

import util.Debug;

public enum TokenType {
    // Identifiers
    Identifier("IDENFR"),

    // Literals
    IntLiteral("INTCON"),
    StringLiteral("STRCON"),
    CharLiteral("CHRCON"),

    // Keywords
    MainKeyword("MAINTK"),
    ConstKeyword("CONSTTK"),

    IntKeyword("INTTK"),
    CharKeyword("CHARTK"),
    VoidKeyword("VOIDTK"),

    BreakKeyword("BREAKTK"),
    ContinueKeyword("CONTINUETK"),
    IfKeyword("IFTK"),
    ElseKeyword("ELSETK"),
    ForKeyword("FORTK"),

    ReturnKeyword("RETURNTK"),

    // I/O
    GetIntKeyword("GETINTTK"),
    GetCharKeyword("GETCHARTK"),
    PrintfKeyword("PRINTFTK"),

    // Operators
    AssignOperator("ASSIGN"),   // =

    NotOperator("NOT"),         // !
    AndOperator("AND"),         // &&
    OrOperator("OR"),           // ||

    PlusOperator("PLUS"),       // +
    MinusOperator("MINU"),      // -
    MultiplyOperator("MULT"),   // *
    DivideOperator("DIV"),      // /
    ModOperator("MOD"),         // %

    LTOperator("LSS"),          // <
    LEOperator("LEQ"),          // <=
    GTOperator("GRE"),          // >
    GEOperator("GEQ"),          // >=

    EQOperator("EQL"),          // ==
    NEOperator("NEQ"),          // !=

    // Punctuations
    Semicolon("SEMICN"),        // ;
    Comma("COMMA"),             // ,

    // Delimiters
    LParenthesis("LPARENT"),    // (
    RParenthesis("RPARENT"),    // )
    LBracket("LBRACK"),         // [
    RBracket("RBRACK"),         // ]
    LBrace("LBRACE"),           // {
    RBrace("RBRACE");           // }


    private final String displayName;

    TokenType(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() {
        if (Debug.DEBUG_STATE) {
            return Debug.TERM_CYAN + super.toString() + Debug.TERM_RESET;
        }
        return displayName;
    }
}
