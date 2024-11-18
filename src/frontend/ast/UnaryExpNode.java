package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.instr.*;
import ir.type.IRBasicType;
import symbol.Func;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.ValueType;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp}
 */
public class UnaryExpNode extends ASTNode {
    public enum Type {
        Primary, FuncCall, Unary
    }

    private Type type;
    private PrimaryExpNode primaryExp;
    private Ident identifier;
    private FuncCallParamsNode funcCallParams;
    private UnaryOp unaryOp;
    private UnaryExpNode unaryExp;
    private int lineNum;

    public UnaryExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    public void parse() {
        Token token = tokens.get();
        lineNum = token.getLineNumber();
        if (token.isTypeOf(TokenType.PlusOperator) ||
                token.isTypeOf(TokenType.MinusOperator) ||
                token.isTypeOf(TokenType.NotOperator)) {
            // UnaryOp UnaryExp
            type = Type.Unary;
            unaryOp = new UnaryOp(token.getType());
            tokens.advance();
            unaryExp = new UnaryExpNode(tokens, depth + 1);
            unaryExp.parse();
        } else if (token.isTypeOf(TokenType.Identifier) && tokens.get(1).isTypeOf(TokenType.LParenthesis)) {
            // FuncCall
            type = Type.FuncCall;
            identifier = new Ident(token.getContent());
            tokens.advance();
            expect(TokenType.LParenthesis, "(");
            if (isExpr(tokens.get().getType())) {
                funcCallParams = new FuncCallParamsNode(tokens, depth + 1);
                funcCallParams.parse();
            }
            expect(TokenType.RParenthesis, ")");
        } else {
            // PrimaryExp
            type = Type.Primary;
            primaryExp = new PrimaryExpNode(tokens, depth + 1);
            primaryExp.parse();
        }
    }

    private boolean isExpr(TokenType type) {
        return type == TokenType.PlusOperator ||
                type == TokenType.MinusOperator ||
                type == TokenType.NotOperator ||
                type == TokenType.LParenthesis ||
                type == TokenType.Identifier ||
                type == TokenType.IntLiteral ||
                type == TokenType.CharLiteral;
    }

    public void analyzeSemantic(SymbolTable table) {
        switch (type) {
            case Primary -> primaryExp.analyzeSemantic(table);
            case FuncCall -> {
                if (!table.hasSymbol(identifier.name())) {
                    // is using undeclared identifier
                    ErrorCollector.getInstance().addError(
                            new CompileError(lineNum, ErrorType.UndefinedSymbol,
                                    "undeclared symbol: " + identifier.name())
                    );
                    return;
                }
                // check if this func call is another func call's parameter
                if (table.isAnalyzeFuncCallParams()) {
                    if (table.isInLValBracket()) {
                        if (table.getBracketStatus()) {
                            table.toggleBracketStatus();
                        } else {
                            checkFuncCallParams(table);
                        }
                    } else {
                        checkFuncCallParams(table);
                    }
                }
                if (funcCallParams == null) {
                    Func func = (Func) table.find(identifier.name());
                    if (func.getParamsNum() != 0) {
                        // call parameterized function without parameters
                        ErrorCollector.getInstance().addError(
                                new CompileError(
                                        lineNum,
                                        ErrorType.ParamNumMismatch,
                                        "parameters expected"
                                )
                        );
                    }
                    return;
                }
                funcCallParams.analyzeSemantic(table, identifier.name());
            }
            case Unary -> unaryExp.analyzeSemantic(table);
        }
    }

    private void checkFuncCallParams(SymbolTable table) {
        Func funcToAnalyze = table.getFuncToCall();
        if (table.getParamsIndex() < 0 || table.getParamsIndex() >= funcToAnalyze.getParamsNum()) {
            throw new RuntimeException("Wrong reserved function parameter number, expected at most " + (funcToAnalyze.getParamsNum() - 1) + " got " + table.getParamsIndex());
        }
        if (funcToAnalyze.getIsParamsArray().get(table.getParamsIndex())) {
            // the index-th parameter of reserved function is an array
            // then current func call is trying to pass variable into array
            ErrorCollector.getInstance().addError(
                    new CompileError(lineNum, ErrorType.ParamTypeMismatch,
                            "expected array type parameter, got func call")
            );
        }
    }

    public int evaluate(SymbolTable table) {
        int res;
        if (type == Type.Primary) {
            res = primaryExp.evaluate(table);
        } else if (type == Type.Unary) {
            assert unaryOp.type() != TokenType.NotOperator;
            res = (unaryOp.type() == TokenType.PlusOperator ? 1 : -1) * unaryExp.evaluate(table);
        } else {
            throw new RuntimeException("Func call should not appear in a const expression");
        }
        return res;
    }

    public IRValue generateIR(SymbolTable table) {
        if (type == Type.Primary) {
            return primaryExp.generateIR(table);
        } else if (type == Type.FuncCall) {
            return generateIRForFuncCall(table);
        } else {
            // Unary
            IRValue u = unaryExp.generateIR(table);
            IRConstInt zero = new IRConstInt(IRBasicType.I32, 0);
            if (unaryOp.type() == TokenType.MinusOperator) {
                // return 0 - u
                IRInstr sub = new IRArithmetic(
                        IRBuilder.getInstance().localReg(), IRInstrType.Sub,
                        zero, u
                );
                IRBuilder.getInstance().addInstr(sub);
                return sub;
            } else if (unaryOp.type() == TokenType.NotOperator) {
                // notice that '!a' is equal to 'a == 0', return type is i1
                IRInstr compareTo0 = new IRIcmp(IRBuilder.getInstance().localReg(), IRInstrType.Eq, u, zero);
                IRBuilder.getInstance().addInstr(compareTo0);
                // zero extends result to 32 bits
                return new IRTypeCast(IRBuilder.getInstance().localReg(), IRInstrType.Zext, compareTo0, IRBasicType.I32);
            }
            // ignore +
            return u;
        }
    }

    private IRValue generateIRForFuncCall(SymbolTable table) {
        Symbol symbol = table.find(identifier.name());
        Func func = (Func) symbol;
        ArrayList<IRValue> params = new ArrayList<>();
        if (funcCallParams != null) {
            params = funcCallParams.generateIR(table, func);
        }
        // void function cannot have any v-regs
        IRInstr funcCall;
        if (func.getReturnType() == ValueType.Void) {
            funcCall = new IRCall(func.getReturnType().mapToIRType(), "", symbol.getIrValue().name(), params);
        } else {
            funcCall = new IRCall(
                    func.getReturnType().mapToIRType(), IRBuilder.getInstance().localReg(), symbol.getIrValue().name(), params
            );
        }
        IRBuilder.getInstance().addInstr(funcCall);
        return funcCall;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<UnaryExp>\n");
            switch (type) {
                case Primary -> b.append(primaryExp);
                case FuncCall -> {
                    b.append("  ".repeat(depth + 1)).append(identifier).append("()");
                    if (funcCallParams != null) {
                        b.append("\n").append(funcCallParams);
                    }
                }
                case Unary -> b.append("  ".repeat(depth)).append(unaryOp).append(unaryExp);
            }
            return b.toString();
        }
        switch (type) {
            case Primary -> b.append(primaryExp);
            case FuncCall -> {
                b.append(identifier).append("LPARENT (\n");
                if (funcCallParams != null) {
                    b.append(funcCallParams);
                }
                b.append("RPARENT )\n");
            }
            case Unary -> b.append(unaryOp).append(unaryExp);
        }
        b.append("<UnaryExp>\n");
        return b.toString();
    }
}
