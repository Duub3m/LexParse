package LexerPart1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private StringHandler stringHandler;
    private int lineNumber;
    private int charPosition;
    private List<Token> tokens;
    private Map<String, Token.TokenType> keywordMap;
    private Map<String, Token.TokenType> twoCharSymbolMap;
    private Map<Character, Token.TokenType> oneCharSymbolMap;

    public Lexer(String input) {
        this.stringHandler = new StringHandler(input);
        this.lineNumber = 0;
        this.charPosition = 0;
        this.tokens = new ArrayList<>();

        initializeKeywordMap();
        initializeSymbolMaps();
    }

    private void initializeKeywordMap() {
        keywordMap = new HashMap<>();
        keywordMap.put("while", Token.TokenType.WHILE);
        keywordMap.put("if", Token.TokenType.IF);
        keywordMap.put("do", Token.TokenType.DO);
        keywordMap.put("for", Token.TokenType.FOR);
        keywordMap.put("break", Token.TokenType.BREAK);
        keywordMap.put("continue", Token.TokenType.CONTINUE);
        keywordMap.put("else", Token.TokenType.ELSE);
        keywordMap.put("return", Token.TokenType.RETURN);
        keywordMap.put("BEGIN", Token.TokenType.BEGIN);
        keywordMap.put("END", Token.TokenType.END);
        keywordMap.put("print", Token.TokenType.PRINT);
        keywordMap.put("printf", Token.TokenType.PRINTF);
        keywordMap.put("next", Token.TokenType.NEXT);
        keywordMap.put("in", Token.TokenType.IN);
        keywordMap.put("delete", Token.TokenType.DELETE);
        keywordMap.put("getline", Token.TokenType.GETLINE);
        keywordMap.put("exit", Token.TokenType.EXIT);
        keywordMap.put("nextfile", Token.TokenType.NEXTFILE);
        keywordMap.put("function", Token.TokenType.FUNCTION);
    }
    

    private void initializeSymbolMaps() {
        twoCharSymbolMap = new HashMap<>();
        twoCharSymbolMap.put(">=", Token.TokenType.GREATER_EQUAL);
        twoCharSymbolMap.put("++", Token.TokenType.INCREMENT);
        twoCharSymbolMap.put("--", Token.TokenType.DECREMENT);
        twoCharSymbolMap.put("<=", Token.TokenType.LESS_EQUAL);
        twoCharSymbolMap.put("==", Token.TokenType.EQUAL);
        twoCharSymbolMap.put("!=", Token.TokenType.NOT_EQUAL);
        twoCharSymbolMap.put("^=", Token.TokenType.EXPONENT_EQUAL);
        twoCharSymbolMap.put("%=", Token.TokenType.MOD_EQUAL);
        twoCharSymbolMap.put("*=", Token.TokenType.MULTIPLY_EQUAL);
        twoCharSymbolMap.put("/=", Token.TokenType.DIVIDE_EQUAL);
        twoCharSymbolMap.put("+=", Token.TokenType.PLUS_EQUAL);
        twoCharSymbolMap.put("-=", Token.TokenType.MINUS_EQUAL);
        twoCharSymbolMap.put("!~", Token.TokenType.NOT_MATCH);
        twoCharSymbolMap.put("&&", Token.TokenType.LOGICAL_AND);
        twoCharSymbolMap.put(">>", Token.TokenType.APPEND);
        twoCharSymbolMap.put("||", Token.TokenType.LOGICAL_OR);
    twoCharSymbolMap.put("!~", Token.TokenType.NOT_TILDE);

        //ONE CHARACTER SYMBOL HASHMAP

        oneCharSymbolMap = new HashMap<>();
        oneCharSymbolMap.put('{', Token.TokenType.OPEN_BRACE);
        oneCharSymbolMap.put('}', Token.TokenType.CLOSE_BRACE);
        oneCharSymbolMap.put('(', Token.TokenType.OPEN_PAREN);
        oneCharSymbolMap.put(')', Token.TokenType.CLOSE_PAREN);
        oneCharSymbolMap.put('$', Token.TokenType.DOLLAR);
        oneCharSymbolMap.put('~', Token.TokenType.TILDE);
        oneCharSymbolMap.put('=', Token.TokenType.ASSIGN);
        oneCharSymbolMap.put('<', Token.TokenType.LESS_THAN);
        oneCharSymbolMap.put('>', Token.TokenType.GREATER_THAN);
        oneCharSymbolMap.put('!', Token.TokenType.EXCLAMATION);
        oneCharSymbolMap.put('+', Token.TokenType.PLUS);
        oneCharSymbolMap.put('^', Token.TokenType.EXPONENT);
        oneCharSymbolMap.put('-', Token.TokenType.MINUS);
        oneCharSymbolMap.put('?', Token.TokenType.QUESTION_MARK);
        oneCharSymbolMap.put(':', Token.TokenType.COLON);
        oneCharSymbolMap.put('*', Token.TokenType.MULTIPLY);
        oneCharSymbolMap.put('/', Token.TokenType.DIVIDE);
        oneCharSymbolMap.put('%', Token.TokenType.MODULUS);
        oneCharSymbolMap.put(';', Token.TokenType.SEMICOLON);
        oneCharSymbolMap.put('\n', Token.TokenType.SEPARATOR);
        oneCharSymbolMap.put('|', Token.TokenType.PIPE);
        oneCharSymbolMap.put(',', Token.TokenType.COMMA);
    }

    public List<Token> Lex() {
        while (!stringHandler.IsDone()) {
            char currentChar = stringHandler.Peek(0);
    
            if (Character.isWhitespace(currentChar)) {
                // Handle whitespace
                stringHandler.GetChar();
                charPosition++;
            }
                else if (currentChar == '\n') {
                    // Handle linefeed
                    tokens.add(new Token(Token.TokenType.SEPARATOR, null, lineNumber, charPosition));
                    lineNumber++;
                    charPosition = 0;
                    stringHandler.GetChar();
                }
                else if (currentChar == '\r') {
                // Handle carriage return
                stringHandler.GetChar();
            } else if (currentChar == '#') {
                // Handle comments
                skipComment();
            } else if (currentChar == '"') {
                // Handle string literals
                handleStringLiteral();
            } else if (currentChar == '`') {
                // Handle patterns
                handlePattern();
            } else if (Character.isLetter(currentChar)) {
                // Handle words/keywords
                processWord();
            } else if (Character.isDigit(currentChar)) {
                // Handle numbers
                processDigits();
            } else {
                // Handle symbols
                processSymbols();
            }
        }
    
        return tokens;
    }
    

    private void skipComment() {
        // Skip the entire comment until the end of the line
        while (!stringHandler.IsDone() && stringHandler.Peek(0) != '\n') {
            stringHandler.GetChar();
        }
    }

    private void handleStringLiteral() {
        StringBuilder literalBuilder = new StringBuilder();
        stringHandler.GetChar(); // Consume the opening double quote

        while (!stringHandler.IsDone() && stringHandler.Peek(0) != '"') {
            char currentChar = stringHandler.GetChar();

            if (currentChar == '\\') {
                // Handle escaped characters
                literalBuilder.append(currentChar);
                if (!stringHandler.IsDone()) {
                    literalBuilder.append(stringHandler.GetChar());
                }
            } else {
                literalBuilder.append(currentChar);
            }
            charPosition++;
        }

        if (stringHandler.Peek(0) == '"') {
            // Consume the closing double quote
            stringHandler.GetChar();
        } else {
            throw new RuntimeException("String literal is missing a closing double quote at line " + lineNumber + ", position " + charPosition);
        }

        tokens.add(new Token(Token.TokenType.STRING_LITERAL, literalBuilder.toString(), lineNumber, charPosition - literalBuilder.length() - 2));
    }

    private void handlePattern() {
        StringBuilder patternBuilder = new StringBuilder();
        stringHandler.GetChar(); // Consume the opening backtick

        while (!stringHandler.IsDone() && stringHandler.Peek(0) != '`') {
            char currentChar = stringHandler.GetChar();
            patternBuilder.append(currentChar);
            charPosition++;
        }

        if (stringHandler.Peek(0) == '`') {
            // Consume the closing backtick
            stringHandler.GetChar();
        } else {
            throw new RuntimeException("Pattern is missing a closing backtick at line " + lineNumber + ", position " + charPosition);
        }

        tokens.add(new Token(Token.TokenType.PATTERN, patternBuilder.toString(), lineNumber, charPosition - patternBuilder.length() - 2));
    }

    private void processWord() {
        StringBuilder wordBuilder = new StringBuilder();
    
        while (!stringHandler.IsDone() && (Character.isLetterOrDigit(stringHandler.Peek(0)) || stringHandler.Peek(0) == '_')) {
            char currentChar = stringHandler.GetChar();
            wordBuilder.append(currentChar);
            charPosition++;
        }
    
        String word = wordBuilder.toString();
        Token.TokenType tokenType = keywordMap.get(word);
    
        if (tokenType != null) {
            // It's a keyword
            tokens.add(new Token(tokenType, lineNumber, charPosition - word.length()));
        } else {
            // It's a regular word
            tokens.add(new Token(Token.TokenType.WORD, word, lineNumber, charPosition - word.length()));
        }
    }
    

    private void processDigits() {
        StringBuilder numberBuilder = new StringBuilder();
        boolean foundPoint = false;

        while (!stringHandler.IsDone() && (Character.isDigit(stringHandler.Peek(0)) || stringHandler.Peek(0) == '.')) {
            char currentChar = stringHandler.GetChar();
            numberBuilder.append(currentChar);
            charPosition++;

            if (currentChar == '.' && foundPoint) {
                throw new RuntimeException("Error: A number can't have more than one decimal point at line " + lineNumber + ", position " + charPosition);
            } else if (currentChar == '.') {
                foundPoint = true;
            }
        }

        tokens.add(new Token(Token.TokenType.NUMBER, numberBuilder.toString(), lineNumber, charPosition - numberBuilder.length()));
    }

    private void processSymbols() {
        String twoCharSymbol = stringHandler.PeekString(2);
        if (twoCharSymbolMap.containsKey(twoCharSymbol)) {
            tokens.add(new Token(twoCharSymbolMap.get(twoCharSymbol), twoCharSymbol, lineNumber, charPosition));
            stringHandler.GetChar(); // Consume the first character
            stringHandler.GetChar(); // Consume the second character
            charPosition += 2;
        } else {
            char currentChar = stringHandler.GetChar();
            if (oneCharSymbolMap.containsKey(currentChar)) {
                tokens.add(new Token(oneCharSymbolMap.get(currentChar), String.valueOf(currentChar), lineNumber, charPosition - 1));
            } else {
                throw new RuntimeException("Unrecognized symbol at line " + lineNumber + ", position " + charPosition);
            }
            charPosition++;
        }
    }
    
}
