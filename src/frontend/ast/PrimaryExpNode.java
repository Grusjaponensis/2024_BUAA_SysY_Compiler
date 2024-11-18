package frontend.ast;

import exception.*;
import frontend.token.*;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.type.IRBasicType;
import symbol.*;
import util.Debug;

/**
 * {@code PrimaryExp -> '(' Exp ')' | LVal | Number | Character}
 */
public class PrimaryExpNode extends ASTNode {
    public enum Type {
        Exp, LVal, Num, Char
    }

    private ExpNode expNode;
    private LValNode lValNode;
    private Number number;
    private Char character;
    private Type type;
    private int lineNum;

    public PrimaryExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        lineNum = token.getLineNumber();
        if (token.isTypeOf(TokenType.LParenthesis)) {
            type = Type.Exp;
            tokens.advance();
            expNode = new ExpNode(tokens, depth + 1);
            expNode.parse();
            expect(TokenType.RParenthesis, ")");
        } else if (token.isTypeOf(TokenType.Identifier)) {
            type = Type.LVal;
            lValNode = new LValNode(tokens, depth + 1);
            lValNode.parse();
        } else if (token.isTypeOf(TokenType.IntLiteral)) {
            type = Type.Num;
            number = new Number(((IntegerLiteral) token).getValue());
            tokens.advance();
        } else {
            type = Type.Char;
            character = new Char(((CharLiteral) token).getValue());
            tokens.advance();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        switch (type) {
            case Exp -> expNode.analyzeSemantic(table);
            case LVal -> {
                if (!lValNode.analyzeSemantic(table)) {
                    return;
                }
                if (!table.isAnalyzeFuncCallParams()) {
                    return;
                }

                Func funcToAnalyze = table.getFuncToCall();
                boolean isIndexParamArray = funcToAnalyze.getIsParamsArray().get(table.getParamsIndex());
                // when lVal is called like an array (isArrayCall() == true), treat it as a variable
                if (lValNode.isArrayCall()) {
                    // assign variable to array param
                    if (isIndexParamArray) {
                        ErrorCollector.getInstance().addError(
                                new CompileError(lineNum, ErrorType.ParamTypeMismatch,
                                        "expect array type parameter got variable")
                        );
                    }
                } else {
                    if (table.find(lValNode.getName()) instanceof Var var) {
                        ValueType indexParamType = funcToAnalyze.getParamTypes().get(table.getParamsIndex());
                        if (var.isArray()) {
                            // assign array to variable param, or char/int array type mismatch
                            if (!isIndexParamArray || indexParamType != var.getValueType()) {
                                ErrorCollector.getInstance().addError(
                                        new CompileError(lineNum, ErrorType.ParamTypeMismatch)
                                );
                            }
                        } else {
                            // assign variable to array
                            if (isIndexParamArray) {
                                ErrorCollector.getInstance().addError(
                                        new CompileError(lineNum, ErrorType.ParamTypeMismatch,
                                                "expect array type parameter got variable")
                                );
                            }
                        }
                    }
                }
            }
            case Num, Char -> {
                if (!table.isAnalyzeFuncCallParams() || table.isInLValBracket()) {
                    return;
                }
                Func funcToAnalyze = table.getFuncToCall();
                if (funcToAnalyze.getIsParamsArray().get(table.getParamsIndex())) {
                    ErrorCollector.getInstance().addError(
                            new CompileError(lineNum, ErrorType.ParamTypeMismatch,
                                    "expect array type parameter got const literal")
                    );
                }
            }
        }
    }

    public int evaluate(SymbolTable table) {
        if (type == Type.Char) {
            return character.c();
        } else if (type == Type.Num) {
            return number.number();
        } else if (type == Type.Exp) {
            return expNode.evaluate(table);
        } else {
            return lValNode.evaluate(table);
        }
    }

    public IRValue generateIR(SymbolTable table) {
        return switch (type) {
            case Exp -> expNode.generateIR(table);
            case LVal -> lValNode.generateIR(table, false);
            case Num, Char -> new IRConstInt(IRBasicType.I32, (number == null ? character.c() : number.number()));
        };
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<PrimaryExp>\n");
            switch (type) {
                case Exp -> b.append(expNode);
                case LVal -> b.append(lValNode);
                case Num -> b.append("  ".repeat(depth + 1)).append(number);
                case Char -> b.append("  ".repeat(depth + 1)).append(character);
            }
            return b.toString();
        }
        switch (type) {
            case Exp -> b.append("LPARENT (\n").append(expNode).append("RPARENT )\n");
            case LVal -> b.append(lValNode);
            case Num -> b.append(number);
            case Char -> b.append(character);
        }
        b.append("<PrimaryExp>\n");
        return b.toString();
    }
}
