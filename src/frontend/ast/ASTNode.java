package frontend.ast;

import exception.CompileError;
import exception.ErrorCollector;
import exception.ErrorType;
import frontend.token.Token;
import frontend.token.TokenList;
import frontend.token.TokenType;
import util.Debug;

import java.util.ArrayList;

public abstract class ASTNode {
    protected TokenList tokens;
    protected int depth;
    protected final ArrayList<CompileError> errors = new ArrayList<>();

    public ASTNode(TokenList tokens, int depth) {
        this.tokens = tokens;
        this.depth = depth;
    }

    public void addErrors() {
        errors.forEach(e -> ErrorCollector.getInstance().addError(e));
    }

    public void expect(TokenType expectedType, String expectValue) {
        Token token = tokens.get();
        if (!token.isTypeOf(expectedType) || !token.getContent().equals(expectValue)) {
            if (expectedType == TokenType.Semicolon) {
                // error of type i: missing semicolon
                int lineNum = tokens.prev().getLineNumber();
                errors.add(new CompileError(lineNum, ErrorType.MissSemicn, "got: " + token.getType()));
                return;
            } else if (expectedType == TokenType.RParenthesis) {
                // error of type j: missing right parenthesis
                int lineNum = tokens.prev().getLineNumber();
                errors.add(new CompileError(lineNum, ErrorType.MissRparent, "got: " + token.getType()));
                return;
            } else if (expectedType == TokenType.RBracket) {
                // error of type k: missing right bracket
                int lineNum = tokens.prev().getLineNumber();
                errors.add(new CompileError(lineNum, ErrorType.MissRbrack, "got: " + token.getType()));
                return;
            }

            throw new RuntimeException(
                    String.format(
                            "Expecting [%s: %s] at line %d but we got [%s: %s]%n",
                            Debug.TERM_BLUE + expectedType,
                            expectValue + Debug.TERM_RESET,
                            token.getLineNumber(),
                            token.getType(),
                            token.getContent()
                    )
            );
        }
        tokens.advance();
    }
}
