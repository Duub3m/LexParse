package LexerPart1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import LexerPart1.Parser.VariableReferenceNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;



public class AwkTest {
    private Lexer lexer;
    private Parser parser;

    @Test
    public void testSingleLineString() {
        String input = "Two Words";
        lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        assertEquals(2, tokens.size());
        assertEquals("WORD(Two)", tokens.get(0).toString());
        assertEquals("WORD(Words)", tokens.get(1).toString());
    }

    @Test
    public void testWordsThenNumbers() {
        String input = "word 123";
        lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        assertEquals(2, tokens.size());
        assertEquals("WORD(word)", tokens.get(0).toString());
        assertEquals("NUMBER(123)", tokens.get(1).toString());
    }

    @Test
    public void testNumbersThenWords() {
        String input = "123 word";
        lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        assertEquals(2, tokens.size());
        assertEquals("NUMBER(123)", tokens.get(0).toString());
        assertEquals("WORD(word)", tokens.get(1).toString());
    }
    @Test
    public void testSymbols() {
        String input = "+ - == != * /";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();
        
        assertEquals(6, tokens.size());
        assertEquals("PLUS(+)", tokens.get(0).toString());
        assertEquals("MINUS(-)", tokens.get(1).toString());
        assertEquals("EQUAL", tokens.get(2).getType().toString()); // Expect "EQUAL" (==)
        assertEquals("NOT_EQUAL", tokens.get(3).getType().toString()); // Expect "NOT_EQUAL" (!=)
        assertEquals("MULTIPLY(*)", tokens.get(4).toString());
        assertEquals("DIVIDE(/)", tokens.get(5).toString());
    }

    @Test
    public void testTwoCharSymbols() {
        // Input string containing two-character symbols
        String input = ">= ++ -- <= == != ^= %= *= /= += -= !~ && >> ||";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        // Checks to see if the number of tokens is 16
        assertEquals(16, tokens.size());

        // Checks that token strings for each two character symbol
        assertEquals("GREATER_EQUAL(>=)", tokens.get(0).toString());
        assertEquals("INCREMENT(++)", tokens.get(1).toString());
        assertEquals("DECREMENT(--)", tokens.get(2).toString());
        assertEquals("LESS_EQUAL(<=)", tokens.get(3).toString());
        assertEquals("EQUAL(==)", tokens.get(4).toString());
        assertEquals("NOT_EQUAL(!=)", tokens.get(5).toString());
        assertEquals("EXPONENT_EQUAL(^=)", tokens.get(6).toString());
        assertEquals("MOD_EQUAL(%=)", tokens.get(7).toString());
        assertEquals("MULTIPLY_EQUAL(*=)", tokens.get(8).toString());
        assertEquals("DIVIDE_EQUAL(/=)", tokens.get(9).toString());
        assertEquals("PLUS_EQUAL(+=)", tokens.get(10).toString());
        assertEquals("MINUS_EQUAL(-=)", tokens.get(11).toString());
        assertEquals("NOT_MATCH(!~)", tokens.get(12).toString());
        assertEquals("LOGICAL_AND(&&)", tokens.get(13).toString());
        assertEquals("APPEND(>>)", tokens.get(14).toString());
        assertEquals("LOGICAL_OR(||)", tokens.get(15).toString());
    }

    @Test
    public void testOneCharSymbols() {
        // Input string containing one character symbols
        String input = "{ } ( ) $ ~ = < > ! + ^ - ? : * / % ; | ," ;
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        // Checks that the number of tokens is 21
        assertEquals(21, tokens.size());

        // Checks that the token strings for each one character symbol
        assertEquals("OPEN_BRACE({)", tokens.get(0).toString());
        assertEquals("CLOSE_BRACE(})", tokens.get(1).toString());
        assertEquals("OPEN_PAREN(()", tokens.get(2).toString());
        assertEquals("CLOSE_PAREN())", tokens.get(3).toString());
        assertEquals("DOLLAR($)", tokens.get(4).toString());
        assertEquals("TILDE(~)", tokens.get(5).toString());
        assertEquals("ASSIGN(=)", tokens.get(6).toString());
        assertEquals("LESS_THAN(<)", tokens.get(7).toString());
        assertEquals("GREATER_THAN(>)", tokens.get(8).toString());
        assertEquals("EXCLAMATION(!)", tokens.get(9).toString());
        assertEquals("PLUS(+)", tokens.get(10).toString());
        assertEquals("EXPONENT(^)", tokens.get(11).toString());
        assertEquals("MINUS(-)", tokens.get(12).toString());
        assertEquals("QUESTION_MARK(?)", tokens.get(13).toString());
        assertEquals("COLON(:)", tokens.get(14).toString());
        assertEquals("MULTIPLY(*)", tokens.get(15).toString());
        assertEquals("DIVIDE(/)", tokens.get(16).toString());
        assertEquals("MODULUS(%)", tokens.get(17).toString());
        assertEquals("SEMICOLON(;)", tokens.get(18).toString());
        assertEquals("PIPE(|)", tokens.get(19).toString());
        assertEquals("COMMA(,)", tokens.get(20).toString());
    }

    @Test
    public void testComments() {
        String input = "This is a line\n This is a comment\nAnother line";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        assertEquals(10, tokens.size());

        assertEquals("WORD(This)", tokens.get(0).toString());
        assertEquals("WORD(is)", tokens.get(1).toString());
        assertEquals("WORD(a)", tokens.get(2).toString());
        assertEquals("WORD(line)", tokens.get(3).toString());
        assertEquals("WORD(This)", tokens.get(4).toString());
        assertEquals("WORD(is)", tokens.get(5).toString());
        assertEquals("WORD(a)", tokens.get(6).toString());
        assertEquals("WORD(comment)", tokens.get(7).toString());
        assertEquals("WORD(Another)", tokens.get(8).toString());
        assertEquals("WORD(line)", tokens.get(9).toString());

    }

    @Test
public void testKeywords() { //Verifys the keywords we created in the Hashmap
    String input = "while if do for";
    Lexer lexer = new Lexer(input);
    List<Token> tokens = lexer.Lex();

    assertEquals(4, tokens.size());

    // Check keywords
    assertEquals(Token.TokenType.WHILE, tokens.get(0).getType());
    assertEquals(Token.TokenType.IF, tokens.get(1).getType());
    assertEquals(Token.TokenType.DO, tokens.get(2).getType());
    assertEquals(Token.TokenType.FOR, tokens.get(3).getType());
}

    @Test
    public void testSimpleLexing() { 
        String input = "for while hello do";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.Lex();

        assertEquals(4, tokens.size());
        assertEquals(Token.TokenType.FOR, tokens.get(0).getType()); // Expect "FOR"
        assertEquals(Token.TokenType.WHILE, tokens.get(1).getType()); // Expect "WHILE"
        assertEquals(Token.TokenType.WORD, tokens.get(2).getType()); // Expect "WORD" (hello)
        assertEquals("hello", tokens.get(2).getValue());
        assertEquals(Token.TokenType.DO, tokens.get(3).getType()); // Expect "DO"
    }

        @Test
    public void testOperationNode() {
        // Create nodes for left and right
        Node leftNode = new LiteralNode(5); // Based on leftNode for NodeLiteral Class
        Node rightNode = new LiteralNode(3); // Based on rightNode for NodeLiteral Class

        // Create an OperationNode with the ADD operation
        OperationNode operationNode = new OperationNode(leftNode, Optional.of(rightNode), OperationType.ADD);

        // Verify that the initialization is correct
        assertEquals(leftNode, operationNode.getLeft());
        assertTrue(operationNode.getRight().isPresent());
        assertEquals(rightNode, operationNode.getRight().get());
        assertEquals(OperationType.ADD, operationNode.getOperationType());
    }
//Tests the PLUS and NUMBER operation
    @Test
public void testParseOperation() throws Parser.ParseException {
    LinkedList<Token> tokens = new LinkedList<>();
    tokens.add(new Token(Token.TokenType.PLUS, "+", 1, 1));
    tokens.add(new Token(Token.TokenType.NUMBER, "5", 1, 2));
    
    Parser parser = new Parser(tokens);
    Optional<Node> result = parser.ParseOperation();
    
    assertTrue(result.isPresent());
    assertEquals(OperationNode.class, result.get().getClass());
    assertEquals(OperationType.ADD, ((OperationNode)result.get()).getOperationType());
}

@Test
public void testParseFactor() {
    List<Token> tokens = Arrays.asList(
        new Token(Token.TokenType.NUMBER, "42", 0, 0)
    );

    Parser parser = new Parser(tokens);
    Optional<Node> result = parser.ParseFactor();

    assertTrue(result.isPresent());

    ConstantNode constantNode = (ConstantNode) result.get();
    assertEquals(42.0, constantNode.getValue());
}

@Test
public void testSpecialParsing() {
    String input = "getline;\nprint;\nprintf;\nexit;\nnextfile;\nnext;";
    lexer = new Lexer(input);
    // Checks to see if ParseFunctionCall() returns a FunctionCallNode
    FunctionCallNode getlineNode = parser.ParseFunctionCall();
    FunctionCallNode printNode = parser.ParseFunctionCall();
    FunctionCallNode printfNode = parser.ParseFunctionCall();
    FunctionCallNode exitNode = parser.ParseFunctionCall();
    FunctionCallNode nextfileNode = parser.ParseFunctionCall();
    FunctionCallNode nextNode = parser.ParseFunctionCall();

    // Checks to see if each node is correct
    assertNotNull(getlineNode);
    assertNotNull(printNode);
    assertNotNull(printfNode);
    assertNotNull(exitNode);
    assertNotNull(nextfileNode);
    assertNotNull(nextNode);
}

public void IntTest(String[] args) {
    Interpreter interpreter = new Interpreter();
    VariableReferenceNode variableReferenceNode = new VariableReferenceNode("name", Optional.empty());
    OperationNode operationNode = new OperationNode(
            OperationType.ADD,
            Optional.of(new ConstantNode(2)),
            Optional.of(new ConstantNode(2))
    );
    OperationNode assignmentNode = new OperationNode(OperationType.ASSIGNMENT, Optional.of(variableReferenceNode), Optional.of(operationNode));

    InterpreterDataType result = interpreter.GetIDT(assignmentNode, null);

   
    // Print value of 'name' in global variables
    System.out.println("The value of 'name' in the global variables is: " + result);
}

@Test
public void LoopTest() {
    List<String> lines = Arrays.asList("while (i < 5) ");
    Interpreter interpreter = new Interpreter(lines);

    //local variables to run the test
    HashMap<String, InterpreterDataType> locals = new HashMap<>();

    // Interprets the while loop
    interpreter.InterpretListOfStatements(locals, new WhileNode);

    // checks if the loop worked
    assertEquals("5", locals.get("i").toString());
}

@Test
public void testInterpreter() {
    List<String> lines = Arrays.asList("x = 2", "y = 6");
    //"local" is created to run the test
    HashMap<String, InterpreterDataType> locals = new HashMap<>();

    // checks to see if x and y have the right number values
    assertEquals("2", locals.get("x").toString());
    assertEquals("6", locals.get("y").toString());
}
}

