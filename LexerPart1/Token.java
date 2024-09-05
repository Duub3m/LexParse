package LexerPart1;

public class Token {
    public enum TokenType {
        PLUS, MINUS, TIMES, DIVIDE, EQUAL, NUMBER, WORD, SEPARATOR, STRING_LITERAL, PATTERN,
        GREATER_EQUAL, INCREMENT, DECREMENT, LESS_EQUAL, NOT_EQUAL, EXPONENT_EQUAL,
        MOD_EQUAL, MULTIPLY_EQUAL, DIVIDE_EQUAL, PLUS_EQUAL, MINUS_EQUAL, NOT_MATCH, LOGICAL_AND,
        APPEND, LOGICAL_OR, OPEN_BRACE, CLOSE_BRACE, OPEN_PAREN, CLOSE_PAREN, DOLLAR, TILDE,
        WHILE, IF, DO, FOR, BREAK, CONTINUE, ELSE, RETURN, BEGIN, END, PRINT, PRINTF, NEXT, IN, DELETE, GETLINE, EXIT, NEXTFILE, FUNCTION,
        LESS_THAN, GREATER_THAN, EXCLAMATION, QUESTION_MARK, COLON, MODULUS, SEMICOLON, PIPE, COMMA, ASSIGN, EXPONENT, MULTIPLY, UNKNOWN, CONCATENATION, NOT_TILDE
    }

    private TokenType type;
    private String value;
    private int lineNumber;
    private int charPosition;

    public Token(TokenType type, int lineNumber, int charPosition) {
        this.type = type;
        this.value = null; // Value is null for certain tokens like separator
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;
    }

    public Token(TokenType type, String value, int lineNumber, int charPosition) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getCharPosition() {
        return charPosition;
    }

    @Override
    public String toString() {
        if (value != null) {
            return type + "(" + value + ")";
        } else {
            return type.toString();
        }
    }
}
