package LexerPart1;

import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {
    private LinkedList<Token> tokens;

    public TokenManager(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    // Peek ahead and return the token 
    public Optional<Token> Peek(int j) {
        if (j >= 0 && j < tokens.size()) {
            return Optional.of(tokens.get(j));
        } else {
            return Optional.empty();
        }
    }

    // Looks in the List and then checks if theres more tokens 
    public boolean MoreTokens() {
        return !tokens.isEmpty();
    }

    //Uses peek to look at the head of the list and remove it. Only gets removed if it matches the token type
    public Optional<Token> MatchAndRemove(Token.TokenType t) {
        if (!tokens.isEmpty() && tokens.peek().getType() == t) {
            return Optional.of(tokens.poll());
        } else {
            return Optional.empty();
        }
    }
}
