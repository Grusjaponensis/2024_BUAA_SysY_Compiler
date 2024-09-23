package frontend;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.*;

import java.util.HashMap;
import java.util.Set;

public class Lexer {
    private final String input;

    private int pos = 0;

    private Token currentToken;

    private int line = 1;

    private int col = 1;

    public Lexer(String input) {
        this.input = input;
    }

    /**
     * Advances to the next token in the input stream.
     *
     * @throws CompileError if an error occurs while lexing the next token
     */
    public void nextToken() throws CompileError {
        currentToken = lex();
    }

    /**
     * Retrieves the current token without advancing the lexer.
     * If there is no current token, it will lex the next token.
     *
     * @return the current token
     * @throws CompileError if an error occurs while lexing the next token
     */
    public Token peek() throws CompileError {
        if (currentToken == null) {
            currentToken = lex();
        }
        return currentToken;
    }

    /**
     * Lex the source code into tokens
     * @return {@code null} if reached end of file, else {@code Token}
     */
    private Token lex() {
        int length = input.length();
        while (true) {
            // skip all whitespace
            while (pos < length && Character.isWhitespace(input.charAt(pos))) {
                // count line numbers and reset columnNumber
                if (input.charAt(pos) == '\n') {
                    newline();
                }
                advance();
            }

            // skip comments
            if (pos < length && input.charAt(pos) == '/') {
                if (pos + 1 < length && input.charAt(pos + 1) == '/') {
                    // single line comment
                    advance(2); // skip "//"
                    while (pos < length && input.charAt(pos) != '\n') {
                        advance();
                    }
                    continue;
                } else if (pos + 1 < length && input.charAt(pos + 1) == '*') {
                    // multi-line comment
                    advance(2);
                    while (pos + 1 < length && !(input.charAt(pos) == '*' && input.charAt(pos + 1) == '/')) {
                        if (input.charAt(pos) == '\n') {
                            newline();
                        }
                        advance();
                    }
                    // Unterminated multi-line comment
                    if (pos + 1 >= length) {
                        return null;
                    }
                    advance(2); // skip "*/"
                    continue;
                } else {
                    advance();
                    return new Operator(TokenType.DivideOperator, line, col - 1, "/");
                }
            }

            // reach the end of file
            if (pos >= length) {
                return null;
            }

            char currentChar = input.charAt(pos);

            // decimal non-leading zero integer
            if (Character.isDigit(currentChar)) {
                return parseNumber();
            }

            // keywords or identifiers
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return parseWord();
            }

            // operators, punctuations, delimiters
            if (isSymbol(currentChar)) {
                return parseSymbol();
            }

            // char literal
            if (currentChar == '\'') {
                return parseCharLiteral(length);
            }

            // string literal
            if (currentChar == '\"') {
                return parseStringLiteral();
            }

            throw new RuntimeException("Unknown symbol: " + currentChar);
        }
    }

    /**
     * Lex non-leading zero decimal integer
     * @return {@code IntegerLiteral}
     */
    private Token parseNumber() {
        int length = input.length();
        StringBuilder builder = new StringBuilder();
        int startCol = col;
        while (pos < length && Character.isDigit(input.charAt(pos))) {
            builder.append(input.charAt(pos));
            advance();
        }
        return new IntegerLiteral(line, startCol, builder.toString());
    }

    /**
     * Lex identifier or keyword
     * @return {@code Identifier} or {@code Keyword}
     */
    private Token parseWord() {
        int length = input.length();
        StringBuilder builder = new StringBuilder();
        char currentChar;
        int startCol = col;
        while (pos < length) {
            currentChar = input.charAt(pos);
            if ((Character.isLetter(currentChar) || currentChar == '_' || Character.isDigit(currentChar))) {
                builder.append(currentChar);
                advance();
            } else {
                break;
            }
        }
        String word = builder.toString();
        if (keywordMap.containsKey(word)) {
            return new Keyword(keywordMap.get(word), line, startCol, word);
        }
        return new Identifier(line, startCol, word);
    }

    /**
     * Checks if the current character is a valid symbol according to the SysY language rules.
     *
     * @param currentChar The character to check.
     * @return {@code true} if the character is a symbol
     */
    private boolean isSymbol(char currentChar) {
        return symbolSet.contains(currentChar);
    }

    /**
     * Parses a symbol from the input, returning the corresponding Token object.
     *
     * @return The {@code Token} representation of the single character symbol.
     */
    private Token parseSymbol() {
        if (pos >= input.length()) {
            throw new RuntimeException("Unexpected end of input");
        }

        char currentChar = input.charAt(pos);

        // first check for double-character symbols
        if (pos + 1 < input.length()) {
            String twoCharSymbol = "" + currentChar + input.charAt(pos + 1);
            if (doubleCharTokens.containsKey(twoCharSymbol)) {
                advance(2);
                return new Operator(doubleCharTokens.get(twoCharSymbol), line, col - 2, twoCharSymbol);
            }
        }

        // then check for single-character symbols
        if (singleCharTokens.containsKey(currentChar)) {
            advance();
            switch (currentChar) {
                case '(', ')', '[', ']', '{', '}' -> {
                    return new Delimiter(
                            singleCharTokens.get(currentChar), line, col - 1, String.valueOf(currentChar)
                    );
                }
                case '+', '-', '*', '%', '!', '<', '>', '=' -> {
                    return new Operator(
                            singleCharTokens.get(currentChar), line, col - 1, String.valueOf(currentChar)
                    );
                }
                case ';', ',' -> {
                    return new Punctuation(
                            singleCharTokens.get(currentChar), line, col - 1, String.valueOf(currentChar)
                    );
                }
            }
        }

        // handle special cases of '&' and '|'
        if (currentChar == '&' || currentChar == '|') {
            advance();
            String value = currentChar + String.valueOf(currentChar);
            ErrorCollector.getInstance().addError(
                    new CompileError(line, ErrorType.IllegalSymbol, "'" + currentChar + "'")
            );
            return new Operator(doubleCharTokens.get(value), line, col - 2, value);
        }

        throw new RuntimeException("Unexpected symbol: " + currentChar);
    }

    /**
     * Parses a single character literal from the input.
     *
     * @param length the length of the input string
     * @return a {@code CharLiteral} representing the parsed character literal
     * @throws RuntimeException if the character literal is empty or not properly terminated
     */
    private Token parseCharLiteral(int length) {
        if (pos + 1 < length) {
            char nextChar = input.charAt(pos + 1);
            if (nextChar == '\'') {
                // empty char literal
                advance();
                throw new RuntimeException("empty character constant");
            } else if (nextChar == '\\') {
                // escape character
                if (pos + 2 < length) {
                    char nextChar2 = input.charAt(pos + 2);
                    if (pos + 3 < length && input.charAt(pos + 3) != '\'') {
                        throw new RuntimeException("missing terminating ' character");
                    }
                    advance(4);
                    return new CharLiteral(line, col - 3, Character.toString(nextChar) + nextChar2);
                }
                throw new RuntimeException("missing terminating ' character");
            } else {
                advance(3);
                return new CharLiteral(line, col - 2, Character.toString(nextChar));
            }
        }
        throw new RuntimeException("missing terminating ' character");
    }

    /**
     * Lexes a string literal from the input.
     *
     * @return {@code StringLiteral} representing the string literal.
     * @throws RuntimeException if the string literal is not properly terminated.
     */
    private Token parseStringLiteral() {
        StringBuilder builder = new StringBuilder();
        advance();
        char c;
        int length = input.length(), startCol = col;

        while (true) {
            if (pos >= length) {
                throw new RuntimeException("missing terminating '\"' character");
            }
            c = input.charAt(pos);
            if (c == '"') {
                advance();
                break;
            }
            if (c == '\\') {
                if (pos + 1 < length) {
                    char nextChar = input.charAt(pos + 1);
                    switch (nextChar) {
                        case 'a' -> builder.append((char) 7);
                        case 'b' -> builder.append('\b');
                        case 'n' -> builder.append('\n');
                        case 't' -> builder.append('\t');
                        case 'f' -> builder.append('\f');
                        case 'v' -> builder.append((char) 11);
                        case '\\' -> builder.append('\\');
                        case '\"' -> builder.append('\"');
                        case '\'' -> builder.append('\'');
                        case '0' -> builder.append('\0');
                        default -> throw new RuntimeException("invalid escape sequence");
                    }
                    advance(2);
                    continue;
                }
                throw new RuntimeException("missing terminating '\"' character");
            }
            builder.append(c);
            advance();
        }
        return new StringLiteral(line, startCol, builder.toString());
    }

    /**
     * Advances the position and column by 1
     */
    private void advance() {
        pos++;
        col++;
    }

    /**
     * Advances the position and column by a specified number {@code n}
     *
     * @param n the number of characters to advance the position and column by
     */
    private void advance(int n) {
        pos += n;
        col += n;
    }

    /**
     * increments the line number by 1 and resets the column number
     * to 1
     */
    private void newline() {
        line++;
        col = 1;
    }

    private static final HashMap<String, TokenType> keywordMap = new HashMap<>() {
        {
            put("main",     TokenType.MainKeyword);
            put("const",    TokenType.ConstKeyword);
            put("int",      TokenType.IntKeyword);
            put("char",     TokenType.CharKeyword);
            put("void",     TokenType.VoidKeyword);
            put("break",    TokenType.BreakKeyword);
            put("continue", TokenType.ContinueKeyword);
            put("if",       TokenType.IfKeyword);
            put("else",     TokenType.ElseKeyword);
            put("for",      TokenType.ForKeyword);
            put("return",   TokenType.ReturnKeyword);
            put("getint",   TokenType.GetIntKeyword);
            put("getchar",  TokenType.GetCharKeyword);
            put("printf",   TokenType.PrintfKeyword);
        }
    };

    private static final Set<Character> symbolSet = Set.of(
            '>', '<', '=',

            '!', '&', '|',

            '+', '-', '*', '%', '/',

            ';', ',',

            '(', ')', '[', ']', '{', '}'
    );

    private static final HashMap<Character, TokenType> singleCharTokens = new HashMap<>() {
        {
            put('>', TokenType.GTOperator);
            put('<', TokenType.LTOperator);
            put('=', TokenType.AssignOperator);
            put('!', TokenType.NotOperator);

            put(';', TokenType.Semicolon);
            put(',', TokenType.Comma);

            put('+', TokenType.PlusOperator);
            put('-', TokenType.MinusOperator);
            put('*', TokenType.MultiplyOperator);
            put('%', TokenType.ModOperator);

            put('(', TokenType.LParenthesis);
            put(')', TokenType.RParenthesis);
            put('[', TokenType.LBracket);
            put(']', TokenType.RBracket);
            put('{', TokenType.LBrace);
            put('}', TokenType.RBrace);
        }
    };

    private static final HashMap<String, TokenType> doubleCharTokens = new HashMap<>() {
        {
            put(">=", TokenType.GEOperator);
            put("<=", TokenType.LEOperator);
            put("==", TokenType.EQOperator);
            put("!=", TokenType.NEOperator);

            put("&&", TokenType.AndOperator);
            put("||", TokenType.OrOperator);
        }
    };
}
