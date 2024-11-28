package frontend.ast;

import frontend.token.TokenList;
import frontend.token.TokenType;
import ir.IRBuilder;
import ir.IRValue;
import ir.constant.IRConstInt;
import ir.instr.IRIcmp;
import ir.instr.IRInstr;
import ir.instr.IRInstrType;
import ir.instr.IRTypeCast;
import ir.type.IRBasicType;
import symbol.SymbolTable;
import util.Debug;

import java.util.ArrayList;

/**
 * {@code EqExp -> RelExp | EqExp ('==' | '!=') RelExp}</br>
 * Rewrite the grammar to:
 * <pre>
 *     {@code EqExp -> { RelExp ('==' | '!=') } RelExp}
 * </pre>
 */
public class EqExpNode extends ASTNode {
    private final ArrayList<RelExpNode> relExpNodes = new ArrayList<>();
    private final ArrayList<TokenType> operators = new ArrayList<>();

    public EqExpNode(TokenList tokens, int depth) {
        super(tokens, depth);
        operators.add(null);
    }

    public void parse() {
        RelExpNode relExpNode = new RelExpNode(tokens, depth + 1);
        relExpNode.parse();
        relExpNodes.add(relExpNode);

        while (tokens.get().isTypeOf(TokenType.EQOperator) ||
                tokens.get().isTypeOf(TokenType.NEOperator)) {
            operators.add(tokens.get().getType());
            tokens.advance();
            relExpNode = new RelExpNode(tokens, depth + 1);
            relExpNode.parse();
            relExpNodes.add(relExpNode);
        }
    }

    public void analyzeSemantic(SymbolTable table) {
        relExpNodes.forEach(exp -> exp.analyzeSemantic(table));
    }

    public IRValue generateIR(SymbolTable table) {
        IRValue u = relExpNodes.get(0).generateIR(table);
        if (relExpNodes.size() == 1) {
            // Notice to type check before use
            IRInstr icmp = new IRIcmp(
                    IRBuilder.getInstance().localReg(),
                    IRInstrType.Ne,
                    IRTypeCast.typeCast(u, IRBasicType.I32), new IRConstInt(IRBasicType.I32, 0),
                    String.format("icmp: %s %s %s", u.name(), "!=", 0)
            );
            IRBuilder.getInstance().addInstr(icmp);
            return icmp;
        }
        IRValue v;
        for (int i = 1; i < relExpNodes.size(); i++) {
            v = relExpNodes.get(i).generateIR(table);
            // Notice to type check before use
            IRInstr icmp = new IRIcmp(
                    IRBuilder.getInstance().localReg(),
                    operators.get(i) == TokenType.EQOperator ? IRInstrType.Eq : IRInstrType.Ne,
                    IRTypeCast.typeCast(u, IRBasicType.I32), IRTypeCast.typeCast(v, IRBasicType.I32),
                    String.format("icmp: %s %s %s", u.name(), operators.get(i) == TokenType.EQOperator ? "==" : "!=", 0)
            );
            IRBuilder.getInstance().addInstr(icmp);
            u = icmp;
        }
        return u;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (Debug.DEBUG_STATE) {
            String space = "  ".repeat(depth);
            b.append(space).append("<EqExp>\n").append(relExpNodes.get(0));
            for (int i = 1; i < relExpNodes.size(); i++) {
                b.append("\n").append(space).append(operators.get(i)).append("\n").append(relExpNodes.get(i));
            }
            return b.toString();
        }
        b.append(relExpNodes.get(0)).append("<EqExp>\n");
        for (int i = 1; i < relExpNodes.size(); i++) {
            String s = (operators.get(i) == TokenType.EQOperator ? "EQL ==\n" : "NEQ !=\n");
            b.append(s).append(relExpNodes.get(i)).append("<EqExp>\n");
        }
        return b.toString();
    }
}
