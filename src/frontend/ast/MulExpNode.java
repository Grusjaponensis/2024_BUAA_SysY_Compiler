package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.instr.IRArithmetic;
import ir.instr.IRInstr;
import ir.instr.IRInstrType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code MulExp -> { UnaryExp ('*' | '/' | '%') } UnaryExp}
 * </pre>
 */
public class MulExpNode extends ASTNode {
    private final ArrayList<UnaryExpNode> unaryExpNodes = new ArrayList<>();
    private final ArrayList<TokenType> operators = new ArrayList<>();

    public MulExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
        // the first term does not have an operator
        operators.add(null);
    }

    public void parse() {
        UnaryExpNode unaryExpNode = new UnaryExpNode(tokens, depth + 1);
        unaryExpNodes.add(unaryExpNode);
        unaryExpNode.parse();
        while (tokens.get().isTypeOf(TokenType.MultiplyOperator) ||
                tokens.get().isTypeOf(TokenType.DivideOperator) ||
                tokens.get().isTypeOf(TokenType.ModOperator)) {
            operators.add(tokens.get().getType());
            tokens.advance();
            unaryExpNode = new UnaryExpNode(tokens, depth + 1);
            unaryExpNodes.add(unaryExpNode);
            unaryExpNode.parse();
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        unaryExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    public int evaluate(SymbolTable table) {
        int res = unaryExpNodes.get(0).evaluate(table);
        for (int i = 1; i < unaryExpNodes.size(); i++) {
            if (operators.get(i) == TokenType.MultiplyOperator) {
                // Multiply *
                res *= unaryExpNodes.get(i).evaluate(table);
            } else if (operators.get(i) == TokenType.DivideOperator) {
                // Divide /
                res /= unaryExpNodes.get(i).evaluate(table);
            } else {
                // Mod %
                res %= unaryExpNodes.get(i).evaluate(table);
            }
        }
        return res;
    }

    public IRValue generateIR(SymbolTable table) {
        IRValue u = unaryExpNodes.get(0).generateIR(table);
        for (int i = 1; i < unaryExpNodes.size(); i++) {
            IRValue v = unaryExpNodes.get(i).generateIR(table);
            TokenType operator = operators.get(i);
            // perform the operation
            IRInstr operation = generateArithmeticOperation(u, v, operator);
            IRBuilder.getInstance().addInstr(operation);
            u = operation;
        }
        return u;
    }

    private IRInstr generateArithmeticOperation(IRValue u, IRValue v, TokenType operator) {
        // perform the operator specified operation between u and v
        IRInstrType instrType;
        if (operator == TokenType.MultiplyOperator) {
            instrType = IRInstrType.Mul;
        } else if (operator == TokenType.DivideOperator) {
            instrType = IRInstrType.SDiv;
        } else {
            instrType = IRInstrType.SRem; // Mod %
        }
        return new IRArithmetic(IRBuilder.getInstance().localReg(), instrType, u, v);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<MulExp>\n").append(unaryExpNodes.get(0));
            for (int i = 1; i < unaryExpNodes.size(); i++) {
                b.append("\n").append(space).append(operators.get(i)).append("\n").append(unaryExpNodes.get(i));
            }
            b.append("\n");
            return b.toString();
        }
        b.append(unaryExpNodes.get(0));
        b.append("<MulExp>\n");
        for (int i = 1; i < unaryExpNodes.size(); i++) {
            b.append(operators.get(i) == TokenType.MultiplyOperator ?
                    "MULT *\n" : operators.get(i) == TokenType.DivideOperator ?
                    "DIV /\n" : "MOD %\n"
            );
            b.append(unaryExpNodes.get(i));
            b.append("<MulExp>\n");
        }
        return b.toString();
    }
}
