package frontend.token;

import java.util.List;
import java.util.NoSuchElementException;

public class TokenList {
    private final List<Token> tokens;
    private int index = 0;

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token get() { return tokens.get(index); }

    public Token get(int k) {
        if (index + k >= this.tokens.size()) {
            throw new NoSuchElementException("Cannot retrieve the " + k + "th next token after " + tokens.get(index)
                    + ": only " + (this.tokens.size() - index - 1) + " token(s) available.");
        }
        return tokens.get(index + k);
    }

    public Token prev() { return tokens.get(index - 1); }

    public void advance() { this.index++; }

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }
}
