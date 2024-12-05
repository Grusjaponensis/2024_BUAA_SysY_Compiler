package frontend.ast.stmt;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.ast.ASTNode;
import frontend.ast.ExpNode;
import frontend.ast.StringConst;
import frontend.token.StringLiteral;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.constant.IRConstArray;
import ir.constant.IRConstInt;
import ir.constant.IRString;
import ir.instr.*;
import ir.type.IRArrayType;
import ir.type.IRBasicType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * {@code PrintfStmt -> 'printf' '(' StringConst { ',' Exp } ')' ';'}
 * <p>
 *     Possible error: missing semicolon
 * </p>
 */
public class PrintfStmt extends ASTNode implements Statement {
    private StringConst formattedString;
    private final ArrayList<ExpNode> params = new ArrayList<>();
    private int lineNum;

    public PrintfStmt(TokenList tokens, int depth) {
        super(tokens, depth);
    }

    @Override
    public void parse() {
        expect(TokenType.PrintfKeyword, "printf");
        lineNum = tokens.prev().getLineNumber();
        expect(TokenType.LParenthesis, "(");

        Token token = tokens.get();
        if (!token.isTypeOf(TokenType.StringLiteral)) {
            throw new RuntimeException("String literal expected, got: " + token);
        }
        formattedString = new StringConst(token.getContent());
        tokens.advance();

        if (!tokens.get().isTypeOf(TokenType.RParenthesis)) {
            while (tokens.get().isTypeOf(TokenType.Comma)) {
                tokens.advance();
                ExpNode param = new ExpNode(tokens, depth + 1);
                param.parse();
                params.add(param);
            }
        }
        expect(TokenType.RParenthesis, ")");
        expect(TokenType.Semicolon, ";");
    }

    @Override
    public void analyzeSemantic(SymbolTable table) {
        if (formattedString.paramsNum() != params.size()) {
            ErrorCollector.getInstance().addError(
                    new CompileError(lineNum, ErrorType.FormStringMismatch,
                            "expected " + formattedString.paramsNum() + " found " + params.size())
            );
            return;
        }
        params.forEach(e -> e.analyzeSemantic(table));
    }

    @Override
    public void generateIR(SymbolTable table) {
        ArrayList<String> splitString = formattedString.separate();
        // used for counting format args
        int index = 0;
        for (String arg : splitString) {
            if (arg.equals("%d")) {
                IRValue value = params.get(index++).generateIR(table);
                IRBuilder.getInstance().addInstr(new IRPutInt(IRTypeCast.typeCast(value, IRBasicType.I32)));
            } else if (arg.equals("%c")) {
                IRValue value = params.get(index++).generateIR(table);
                IRBuilder.getInstance().addInstr(new IRPutCh(IRTypeCast.typeCast(value, IRBasicType.I32)));
            } else {
                ArrayList<Integer> initVals = arg.chars().boxed().collect(Collectors.toCollection(ArrayList::new));
                // add trailing '\0' to end the string
                initVals.add(0);
                IRGlobal str = new IRGlobal(
                        new IRArrayType(arg.length() + 1, IRBasicType.I8),
                        IRBuilder.getInstance().stringReg(), // e.g., .str_1 / .str_2 / ...
                        true,
                        new IRConstArray(IRBasicType.I8, initVals, IRBasicType.I8)
                );
                IRBuilder.getInstance().addGlobalVar(str);
                IRInstr getElemPtr = new IRGetElemPtr(
                        IRBasicType.I8,
                        IRBuilder.getInstance().localReg(),
                        str, new IRConstInt(IRBasicType.I32, 0), "ptr: " + str.name()
                );
                IRBuilder.getInstance().addInstr(getElemPtr);
                IRBuilder.getInstance().addInstr(
                        new IRPutStr(
                                getElemPtr, new IRString(str.name().substring(1), initVals), // remove leading '@' character
                                "value: \"" + StringLiteral.display(arg) + "\""
                        )
                );
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        addErrors();
        if (Debug.DEBUG_STATE) {
            b.append("  ".repeat(depth)).append("<PrintfStmt>\n")
                    .append("  ".repeat(depth + 1)).append(formattedString).append("\n");
            params.forEach(b::append);
            return b.toString();
        }
        b.append("PRINTFTK printf\nLPARENT (\n").append(formattedString);
        params.forEach(e -> b.append("COMMA ,\n").append(e));
        b.append("RPARENT )\n").append("SEMICN ;\n");
        return b.toString();
    }
}
