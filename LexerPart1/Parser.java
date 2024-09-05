package LexerPart1;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

public class Parser {

    public FunctionCallNode ParseFunctionCall() {
        if (tokenManager.MatchAndRemove(Token.TokenType.GETLINE).isPresent()) {
            //getline function
            return new FunctionCallNode("getline", Collections.emptyList());
        } else if (tokenManager.MatchAndRemove(Token.TokenType.PRINT).isPresent()) {
            //print function
            return new FunctionCallNode("print", Collections.emptyList());
        } else if (tokenManager.MatchAndRemove(Token.TokenType.PRINTF).isPresent()) {
            //printf function
            return new FunctionCallNode("printf", Collections.emptyList());
        } else if (tokenManager.MatchAndRemove(Token.TokenType.EXIT).isPresent()) {
            //exit function
            return new FunctionCallNode("exit", Collections.emptyList());
        } else if (tokenManager.MatchAndRemove(Token.TokenType.NEXTFILE).isPresent()) {
            //nextfile function
            return new FunctionCallNode("nextfile", Collections.emptyList());
        } else if (tokenManager.MatchAndRemove(Token.TokenType.NEXT).isPresent()) {
            //next function
            return new FunctionCallNode("next", Collections.emptyList());
        }
        return null;
    }
    
//ParseOr
//Parser method for logical_OR which parses as well as combine expressions using || operators 
    public Optional<Node> ParseOr() {
        Optional<Node> left = ParseAnd();
    
        while (true) {
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.LOGICAL_OR);
    
            if (!op.isPresent()) {
                return left;
            }
    
            Optional<Node> right = ParseAnd();
            OperationType operation = OperationType.LOGICAL_OR;
    
            left = Optional.of(new MathOpNode(left.get(), operation, right.get()));
        }
    }
    
//ParseAND
//parses through to see if theres and any combining expressions with LOGICAL_AND(&&) in the input
    public Optional<Node> ParseAnd() {
        Optional<Node> left = ParseMatch();
    
        while (true) {
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.LOGICAL_AND);
    
            if (!op.isPresent()) {
                return left;
            }
    
            Optional<Node> right = ParseMatch();
            OperationType operation = OperationType.LOGICAL_AND;
    
            left = Optional.of(new MathOpNode(left.get(), operation, right.get()));
        }
    }
    

//ParseMatch
//Parse through operations related to matching TILDE(~) and also NOT_TILDE(!~)
    public Optional<Node> ParseMatch() {
        Optional<Token> matchToken = tokenManager.MatchAndRemove(Token.TokenType.TILDE);
        if (matchToken.isPresent()) {
            Optional<Node> expression = ParseExpression();
            if (expression.isPresent()) {
                return Optional.of(new OperationNode(expression.get(), Optional.empty(), OperationType.MATCH));
            } else {
                throw new RuntimeException(" ~ operator");
            }
        }
    
        Optional<Token> notMatchToken = tokenManager.MatchAndRemove(Token.TokenType.NOT_TILDE);
        if (notMatchToken.isPresent()) {
            Optional<Node> expression = ParseExpression();
            if (expression.isPresent()) {
                return Optional.of(new OperationNode(expression.get(), Optional.empty(), OperationType.MATCH));
            } else {
                throw new RuntimeException("!~ operator");
            }
        }
    
        return Optional.empty(); // No operator matches
    }
    

// BooleanCompare
// Handles operators likexf <, <=, !=, ==, >, >=
public Optional<Node> ParseBooleanCompare() {
    Optional<Node> left = ParseTerm(); // We're starting with term first for arithmetic operations

    while (true) {
        Optional<OperationType> op = Optional.empty();

        // Check for various comparison operators
        if (tokenManager.MatchAndRemove(Token.TokenType.LESS_THAN).isPresent()) {
            op = Optional.of(OperationType.LT);
        } else if (tokenManager.MatchAndRemove(Token.TokenType.LESS_EQUAL).isPresent()) {
            op = Optional.of(OperationType.LE);
        } else if (tokenManager.MatchAndRemove(Token.TokenType.NOT_EQUAL).isPresent()) {
            op = Optional.of(OperationType.NE);
        } else if (tokenManager.MatchAndRemove(Token.TokenType.EQUAL).isPresent()) {
            op = Optional.of(OperationType.EQ);
        } else if (tokenManager.MatchAndRemove(Token.TokenType.GREATER_THAN).isPresent()) {
            op = Optional.of(OperationType.GT);
        } else if (tokenManager.MatchAndRemove(Token.TokenType.GREATER_EQUAL).isPresent()) {
            op = Optional.of(OperationType.GE);
        }

        if (!op.isPresent()) {
            return left;
        }

        Optional<Node> right = ParseTerm();
        left = Optional.of(new MathOpNode(left.get(), op.get(), right.get()));
    }
}

// Concatenation
public Optional<Node> ParseConcatenation() {
    Optional<Node> left = ParseFactor();

    while (true) {
        Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.CONCATENATION);

        if (!op.isPresent()) {
            return left;
        }

        Optional<Node> right = ParseFactor();
        OperationType operation = OperationType.CONCATENATION;

        left = Optional.of(new MathOpNode(left.get(), operation, right.get()));
    }
}


// Factor
public Optional<Node> ParseFactor() {
    Optional<Token> num = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
    if (num.isPresent()) {
        double value = Double.parseDouble(num.get().getValue());
        return Optional.of(new ConstantNode(value));
    }

    if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
        Optional<Node> expression = ParseExpression();
        if (expression.isPresent() && tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
            return expression;
        } else {
            throw new RuntimeException("No closing parenthesis");
        }
    }

    return Optional.empty(); // Invalid factor
}

// Term 
public Optional<Node> ParseTerm() {
    Optional<Node> left = ParseFactor();

    while (true) {
        Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.TIMES);
        if (!op.isPresent()) {
            op = tokenManager.MatchAndRemove(Token.TokenType.DIVIDE);
        }

        if (!op.isPresent()) {
            return left;
        }

        Optional<Node> right = ParseFactor();
        OperationType operation = (op.get().getType() == Token.TokenType.TIMES) ?
            OperationType.MULTIPLY : OperationType.DIVIDE;

        left = Optional.of(new MathOpNode(left.get(), operation, right.get()));
    }
}

// Expression 
public Optional<Node> ParseExpression() {
    Optional<Node> left = ParseTerm();

    while (true) {
        Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.PLUS);
        if (!op.isPresent()) {
            op = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
        }

        if (!op.isPresent()) {
            return left;
        }

        Optional<Node> right = ParseTerm();
        OperationType operation = (op.get().getType() == Token.TokenType.PLUS) ?
            OperationType.ADD : OperationType.SUBTRACT;

        left = Optional.of(new MathOpNode(left.get(), operation, right.get()));
    }
}

    //ParseBottomLevel
    public Optional<Node> ParseBottomLevel() {

        // Check for PostIncrement or PostDecrement
        if (tokenManager.MatchAndRemove(Token.TokenType.INCREMENT).isPresent()) {
            Optional<Node> result = ParseBottomLevel();
            return Optional.of(new OperationNode(result.get(), Optional.empty(), OperationType.POSTINC));
        } else if (tokenManager.MatchAndRemove(Token.TokenType.DECREMENT).isPresent()) {
            Optional<Node> result = ParseBottomLevel();
            return Optional.of(new OperationNode(result.get(), Optional.empty(), OperationType.POSTDEC));
        }

    //If STRING_LITERAL is found then we create a ConstantNode with its value 
        if (tokenManager.MatchAndRemove(Token.TokenType.STRING_LITERAL).isPresent()) {
            String value = tokenManager.Peek(-1).get().getValue();
            return Optional.of(new ConstantNode(value));
        }
    //if NUMBER is found then we create a ConstantNode with its value 
        if (tokenManager.MatchAndRemove(Token.TokenType.NUMBER).isPresent()) {
            double value = Double.parseDouble(tokenManager.Peek(-1).get().getValue());
            return Optional.of(new ConstantNode(value));
        }
    //If PATTERN is found then we create a PatternNode with its value 
        if (tokenManager.MatchAndRemove(Token.TokenType.PATTERN).isPresent()) {
            String value = tokenManager.Peek(-1).get().getValue();
            return Optional.of(new PatternNode(value));
        }
    // If LPAREN (OPEN_PAREN) is present then it parses an operation
        if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent() && tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                return operation;
            }
        }
    // If NOT (EXCLAMATION) is present then it parses an operation
        if (tokenManager.MatchAndRemove(Token.TokenType.EXCLAMATION).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent()) {
                return Optional.of(new OperationNode(operation.get(), null, OperationType.NOT));
            }
        }
    // If MINUS is present then it parses an operation
        if (tokenManager.MatchAndRemove(Token.TokenType.MINUS).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent()) {
                return Optional.of(new OperationNode(operation.get(), null, OperationType.UNARYNEG));
            }
        }
    // If PLUS is present then it parses an operation
        if (tokenManager.MatchAndRemove(Token.TokenType.PLUS).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent()) {
                return Optional.of(new OperationNode(operation.get(), null, OperationType.UNARYPOS));
            }
        }

    //If INCREMENT (++) is present then is parses an operation 
        if (tokenManager.MatchAndRemove(Token.TokenType.INCREMENT).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent()) {
                return Optional.of(new OperationNode(operation.get(), null, OperationType.PREINC));
            }
        }
    //IF DECREMENT (--) is present then is parses an operation 
        if (tokenManager.MatchAndRemove(Token.TokenType.DECREMENT).isPresent()) {
            Optional<Node> operation = ParseOperation();
            if (operation.isPresent()) {
                return Optional.of(new OperationNode(operation.get(), null, OperationType.PREDEC));
            }
        }
    
        return ParseLValue();
    }
    
    public Optional<Node> ParseLValue() {
        if (tokenManager.MatchAndRemove(Token.TokenType.DOLLAR).isPresent()) {
            Optional<Node> bottomLevel = ParseBottomLevel();
            if (bottomLevel.isPresent()) {
                return Optional.of(new OperationNode(bottomLevel.get(), null, OperationType.DOLLAR));
            }
        }
    
        if (tokenManager.MatchAndRemove(Token.TokenType.WORD).isPresent()) {
            if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
                Optional<Node> operation = ParseOperation();
                if (operation.isPresent() && tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                    return Optional.of(new VariableReferenceNode(tokenManager.Peek(-3).get().getValue(), operation));
                }
            } else {
                return Optional.of(new VariableReferenceNode(tokenManager.Peek(-1).get().getValue(), Optional.empty()));
            }
        }
    
        return Optional.empty();
    }
    
    
    private TokenManager tokenManager;

    public Parser(LinkedList<Token> tokens) {
        this.tokenManager = new TokenManager(tokens);
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
    // Variable Refrence Node class
    public class VariableReferenceNode extends Node {
        private String name; //Name of the variable 
        private Optional<Node> index; //Optional Node that is the expression for the index. 

    
        public VariableReferenceNode(String name, Optional<Node> index) {
            this.name = name;
            this.index = index;
        }
    
        public String getName() {
            return name;
        }
    
        public Optional<Node> getIndex() {
            return index;
        }
    
        @Override
        public String toString() {
            if (index.isPresent()) {
                return "VariableReferenceNode(name: " + name + ", index: " + index.get() + ")";
            } else {
                return "VariableReferenceNode(name: " + name + ")";
            }
        }
    }
    



    // Program Node class
    public static class ProgramNode {
        private ArrayList<FunctionDefinitionNode> functionDefinitions = new ArrayList<>();
        private ArrayList<BlockNode> beginBlocks = new ArrayList<>();
        private ArrayList<BlockNode> endBlocks = new ArrayList<>();
        private ArrayList<BlockNode> actionBlocks = new ArrayList<>();

        public void addFunction(FunctionDefinitionNode function) {
            functionDefinitions.add(function);
        }

        public void addBeginBlock(BlockNode block) {
            beginBlocks.add(block);
        }

        public void addEndBlock(BlockNode block) {
            endBlocks.add(block);
        }

        public void addActionBlock(BlockNode block) {
            actionBlocks.add(block);
        }
    }

    

    //FunctionDefinitionNode Class 
    public static class FunctionDefinitionNode {
        private String name;
        private LinkedList<String> parameters = new LinkedList<>();
        private LinkedList<StatementNode> statements = new LinkedList<>();

        public FunctionDefinitionNode(String name, LinkedList<String> parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        public void addStatement(StatementNode statement) {
            statements.add(statement);
        }

    }

    // Define the BlockNode class as an inner class
    public static class BlockNode {
        private LinkedList<StatementNode> statements = new LinkedList<>();
        private Optional<Node> condition = Optional.empty();

        public void addStatement(StatementNode statement) {
            statements.add(statement);
        }

        public LinkedList<StatementNode> getStatements() {
            return statements;
        }

        public Optional<Node> getCondition() {
            return condition;
        }

        public void setCondition(Node condition) {
            this.condition = Optional.of(condition);
        }
    }

    //StatementNode Class
    public static abstract class StatementNode extends Node {
            public BlockNode toBlockNode() {
                BlockNode blockNode = new BlockNode();
                //Converts statement node to blockNode
                return blockNode;
            }
            @Override
            public String toString() {
                return "StatementNode";
            }
        }

    public ProgramNode Parse() throws ParseException {
        ProgramNode programNode = new ProgramNode();

        while (tokenManager.MoreTokens()) {
            if (!ParseFunction(programNode) && !ParseAction(programNode)) {
                throw new ParseException("Error - Unexpected token found: " + tokenManager.Peek(0).orElse(new Token(Token.TokenType.UNKNOWN, "", 0, 0)));
            }
        }

        return programNode;
    }

    public boolean AcceptSeparators() {
        boolean foundSeparator = false;
    
        while (tokenManager.MatchAndRemove(Token.TokenType.SEPARATOR).isPresent()) {
            foundSeparator = true;
        }
    
        return foundSeparator;
    }
    

    private boolean ParseFunction(ProgramNode programNode) {
        if (tokenManager.MatchAndRemove(Token.TokenType.FUNCTION).isPresent()) {
            Optional<Token> functionNameToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
    
        if (functionNameToken.isPresent()) {
                String functionName = functionNameToken.get().getValue();
    
        if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
                    LinkedList<String> parameters = new LinkedList<>();
    
        while (true) {
        Optional<Token> parameterToken = tokenManager.MatchAndRemove(Token.TokenType.WORD);
        if (parameterToken.isPresent()) {
            parameters.add(parameterToken.get().getValue());
        } else {
         break;
        }
    
        if (!AcceptSeparators()) {
        break;
        }
                }
    
        if (tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
            FunctionDefinitionNode functionNode = new FunctionDefinitionNode(functionName, parameters);
            BlockNode functionBlock = ParseBlock();
            functionBlock.getStatements().forEach(functionNode::addStatement);
            programNode.addFunction(functionNode);
    
            return true;
                }
            }
        }
        }
    
        return false; // Not a function
    }
    
    
    

    private boolean ParseAction(ProgramNode programNode) {
        if (tokenManager.MatchAndRemove(Token.TokenType.BEGIN).isPresent()) {
            BlockNode beginBlock = ParseBlock();
            programNode.addBeginBlock(beginBlock);
            return true;
        }
    
        if (tokenManager.MatchAndRemove(Token.TokenType.END).isPresent()) {
            BlockNode endBlock = ParseBlock();
            programNode.addEndBlock(endBlock);
            return true;
        }
    
        Optional<Node> operation = ParseOperation();
        if (operation.isPresent()) {
            BlockNode actionBlock = ParseBlock();
            programNode.addActionBlock(actionBlock);
            return true;
        }
    
        return false; 
    }
    

//ParseOperation - Parser 1: Just returns an empty optional for now. (Note: Later on work on allowing it to return the AST representation)
    public Optional<Node> ParseOperation() {
        Optional<Node> left = ParseBottomLevel();

        while (true) {
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.EXPONENT);
    
            if (!op.isPresent()) {
                return left;
            }
    
            Optional<Node> right = ParseBottomLevel();
            left = Optional.of(new MathOpNode(left.get(), OperationType.EXPONENT, right.get()));
            // Parses through different types of operations(Ex: Increment, DECREMENT, MINUS...)
            Optional<Node> operation = ParseBottomLevel();
            // Increment 
            if (tokenManager.MatchAndRemove(Token.TokenType.INCREMENT).isPresent()) {
            // Decrement
                operation = Optional.of(new OperationNode(operation.get(), Optional.empty(), OperationType.PREINC));
            } else if (tokenManager.MatchAndRemove(Token.TokenType.DECREMENT).isPresent()) {
                // Decrement operation
                operation = Optional.of(new OperationNode(operation.get(), Optional.empty(), OperationType.PREDEC));
            } else if (tokenManager.MatchAndRemove(Token.TokenType.MINUS).isPresent()) {
                // Handle unary negation
                operation = Optional.of(new OperationNode(operation.get(), Optional.empty(), OperationType.UNARYNEG));
            } else if (tokenManager.MatchAndRemove(Token.TokenType.PLUS).isPresent()) {
                // Handle unary positive
                operation = Optional.of(new OperationNode(operation.get(), Optional.empty(), OperationType.UNARYPOS));
            } else if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
                // Handle array subscripting
                Optional<Node> index = ParseOperation();
                if (tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                    operation = Optional.of(new VariableReferenceNode("", index));
                } else {
                    // Error for when there is no closing bracket
                    throw new RuntimeException("No closing bracket");
                }
            }
        
            return operation;
        }
    }
        

    private BlockNode ParseBlock() {
        BlockNode blockNode = null; // creates blockNode outside the if block
    
        if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_BRACE).isPresent()) {
            blockNode = new BlockNode();
    
            // Check if it's a single-line block
            StatementNode statement = ParseStatement();
            if (statement != null) {
                blockNode.addStatement(statement);
            } else {
                while (true) {
                    statement = ParseStatement();
                    if (statement != null) {
                        blockNode.addStatement(statement);
                    } else {
                        break;
                    }
                }
            }
    
            if (!tokenManager.MatchAndRemove(Token.TokenType.CLOSE_BRACE).isPresent()) {
                throw new RuntimeException("There is no closing brace");
            }
        }
    
        return blockNode;
    }
    
//ContinueNode Class - Used in StatementNode
class ContinueNode extends StatementNode {
    //Override statment for ContinueNode
    @Override
    public String toString() {
        return "ContinueNode";
    }
}

//BreakNode Class - Used in StatementNode
class BreakNode extends StatementNode {
 //Override statment for BreakNode
    @Override
    public String toString() {
        return "BreakNode";
    }
}
//IfNode Class - Used in StatementNode
class IfNode extends StatementNode {
    private Node condition;
    private BlockNode ifBlock;
    private BlockNode elseBlock;

    public IfNode(Node condition, BlockNode ifBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
        
    }
    //Override statment for IfNode
    @Override
    public String toString() {
        return "BreakNode";
    }
    public Object getCondition() {
        return null;
    }
    public LinkedList<StatementNode> getStatements() {
        return null;
    }
    public IfNode getNextIfNode() {
        return null;
    }

}
//ForNode Class - Used in StatementNode
class ForNode extends StatementNode {
    private Node initialization;
    private Node condition;
    private Node increment;
    private BlockNode blockNode;

    public ForNode(Node initialization, Node condition, Node increment, BlockNode blockNode) {
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.blockNode = blockNode;
    }
    //Override statment for ForNode
    @Override
    public String toString() {
        return "ForNode";
    }
    public StatementNode getInitialization() {
        return null;
    }
    public Node getCondition() {
        return condition;
    }
    public LinkedList<StatementNode> getStatements() {
        return null;
    }
    public StatementNode getIncrement() {
        return null;
    }
}

// DeleteNode class - Used in StatementNode
class DeleteNode extends StatementNode {
    private String target; // Changed from 'name' to 'target'
    private List<Integer> indices;

    // Constructors

    // This constructor only has target and is used for deleting the whole array
    public DeleteNode(String target) {
        this.target = target;
        this.indices = new ArrayList<>();
    }

    // This constructor includes both target and an integer list of indices; it's here for deleting lists of indices
    public DeleteNode(String target, List<Integer> indices) {
        this.target = target;
        this.indices = indices;
    }

    // Getter for target
    public String getTarget() {
        return target;
    }

    // Getter for indices
    public List<Integer> getIndices() {
        return indices;
    }

    // Override statement for DeleteNode
    @Override
    public String toString() {
        return "DeleteNode";
    }
}

//ForEachNode class - Used in StatementNode
class ForEachNode extends StatementNode {
    private String variable;
    private Node iterable;
    private BlockNode blockNode;

    public ForEachNode(String variable, Node iterable, BlockNode blockNode) {
        this.variable = variable;
        this.iterable = iterable;
        this.blockNode = blockNode;
    }
//Override statment for ForEachNode
    @Override
    public String toString() {
        return "ForEachNode";
    }
    public Node getArray() {
        return null;
    }
    public String getVariable() {
        return variable;
    }
    public LinkedList<StatementNode> getStatements() {
        return null;
    }
}
//WhileNode class - Used in StatementNode
class WhileNode extends StatementNode {
    private Node condition;
    private BlockNode whileBlock;

    public WhileNode(Node condition, BlockNode whileBlock) {
        this.condition = condition;
        this.whileBlock = whileBlock;
    }

    public Node getCondition() {
        return condition;
    }

    public BlockNode getWhileBlock() {
        return whileBlock;
    }
//Override statment for WhileNode 
    @Override
    public String toString() {
        return "WhileNode";
    }

    public List<StatementNode> getStatements() {
        return null;
    }
}

//DoWhileNode class - Used in StatementNode
class DoWhileNode extends StatementNode {
    private Node condition;
    private BlockNode doBlock;

    public DoWhileNode(Node condition, BlockNode doBlock) {
        this.condition = condition;
        this.doBlock = doBlock;
    }

    public Node getCondition() {
        return condition;
    }

    public BlockNode getDoBlock() {
        return doBlock;
    }

    //Override statement for DoWhileNode
    @Override
    public String toString() {
        return "DoWhileNode";
    }

    public List<StatementNode> getStatements() {
        return null;
    }
}

//ReturnNode class - Used in StatementNode
//return statement node
class ReturnNode extends StatementNode {
    private Node expression;

    public ReturnNode(Node expression) {
        this.expression = expression;
    }

    public Node getExpression() {
        return expression;
    }
//Override statement for ReturnNode class
    @Override
    public String toString() {
        return "ReturnNode";
    }

    public boolean hasValue() {
        return false;
    }

    public Node getValue() {
        return null;
    }
}






public StatementNode ParseStatement() {
    if (tokenManager.MatchAndRemove(Token.TokenType.CONTINUE).isPresent()) {
        AcceptSeparators(); // If we find CONTINUE then we accept separators
        return new ContinueNode();
    } else if (tokenManager.MatchAndRemove(Token.TokenType.BREAK).isPresent()) {
        AcceptSeparators(); // If we find BREAK then we accept separators
        return new BreakNode();


    } else if (tokenManager.MatchAndRemove(Token.TokenType.IF).isPresent()) {
        if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
            Node ifCondition = ParseOperation().get(); 
            if (tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                BlockNode ifBlock = ParseBlock();
                BlockNode elseBlock = null;

                if (tokenManager.MatchAndRemove(Token.TokenType.ELSE).isPresent()) {
                    if (tokenManager.MatchAndRemove(Token.TokenType.IF).isPresent()) {
                        elseBlock = ParseStatement().toBlockNode();
                    } else {
                        elseBlock = ParseBlock();
                    }
                }

                IfNode ifNode = new IfNode(ifCondition, ifBlock, elseBlock);
                return ifNode;
            }
        }

    
//DeleteNode
// Checks for the DELETE token and then parses the Delete node
// Delete: takes a parameter which is either just a name (delete the whole array) or array reference with a comma separated list of indices. 
    } else if (tokenManager.MatchAndRemove(Token.TokenType.DELETE).isPresent()) {
        if (tokenManager.MatchAndRemove(Token.TokenType.WORD).isPresent()) {
         String name = tokenManager.Peek(-1).get().getValue();
            if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_BRACE).isPresent()) {
                 List<Integer> indices = new ArrayList<>();
                while (true) {
                Optional<Token> index = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
                 if (index.isPresent()) {
                    indices.add(Integer.parseInt(index.get().getValue()));
                     } else {
                     break;
                     }
             tokenManager.MatchAndRemove(Token.TokenType.COMMA);
                    }
            tokenManager.MatchAndRemove(Token.TokenType.CLOSE_BRACE); 
        
                 if (!indices.isEmpty()) {
                return new DeleteNode(name, indices);
                    }
             } else {
                         return new DeleteNode(name);
                    }


 //WhileNode
 // Checks for the WHILE token and then parses the while node
 //While: Has a condition (ParseOperation()) and statements
    } else if (tokenManager.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
         if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
            Node condition = ParseOperation().get();
             if (tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                BlockNode whileBlock = ParseBlock();
                 WhileNode whileNode = new WhileNode(condition, whileBlock);
                            return whileNode;
                        }
                    }
                }
//DOWhileNode
// Checks for both the WHILE and The Do token and then parses the DoWhile node
                else if (tokenManager.MatchAndRemove(Token.TokenType.DO).isPresent()) {
                    BlockNode doBlock = ParseBlock();
                  if (tokenManager.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
                       if (tokenManager.MatchAndRemove(Token.TokenType.OPEN_PAREN).isPresent()) {
                          Node condition = ParseOperation().get();
                           if (tokenManager.MatchAndRemove(Token.TokenType.CLOSE_PAREN).isPresent()) {
                            if (tokenManager.MatchAndRemove(Token.TokenType.SEPARATOR).isPresent()) {
                               DoWhileNode doWhileNode = new DoWhileNode(condition, doBlock);
                              return doWhileNode;
                          }
                      }
                  }
              }
              
                }
//ReturnNode
//Checks for the Return Token and the parses the ReturnNode
//Return: takes a single parameter (ParseOperation()).

            } else if (tokenManager.MatchAndRemove(Token.TokenType.RETURN).isPresent()) {
                Node expression = ParseOperation().get();
                return new ReturnNode(expression);
            }
            
    
            

    return null; // RETURNs Null if there are no statements that match
}

}





