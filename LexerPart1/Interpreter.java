package LexerPart1;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.RowFilter.ComparisonType;
import LexerPart1.Parser.BreakNode;
import LexerPart1.Parser.ContinueNode;
import LexerPart1.Parser.DeleteNode;
import LexerPart1.Parser.DoWhileNode;
import LexerPart1.Parser.ForEachNode;
import LexerPart1.Parser.ForNode;
import LexerPart1.Parser.IfNode;
import LexerPart1.Parser.ReturnNode;
import LexerPart1.Parser.StatementNode;
import LexerPart1.Parser.VariableReferenceNode;
import LexerPart1.Parser.WhileNode;



// Interpreter Class
public class Interpreter {
    
    private HashMap<String, InterpreterDataType> globalVariables; // HashMap<String,IDT> that will hold our global variables
    private LineManager lineManager;

    public Interpreter() {
        this.globalVariables = new HashMap<>();
    }

    class LineManager {
        private List<String> lines;
        private int currentLineIndex;
        private int NR;
        private int FNR;

        public void callSplitAndAssign() {
            SplitAndAssign();
        }

        // Takes a List<String> as a parameter to the constructor and stores it in a member.
        public LineManager(List<String> lines) {
            this.lines = lines;
            this.currentLineIndex = 0;
            this.NR = 0;
            this.FNR = 0;
        }

        // SplitAndAssign - Method that is used to get the next line and split it by looking at the global variables to find Field Separator “FS”
        public boolean SplitAndAssign() {
            if (currentLineIndex >= lines.size()) {
                return false; // if there's nothing to split, it returns false
            }

            String currentLine = lines.get(currentLineIndex);

            // Splitting
            SplitAndAssign();

            // NR and FNR
            NR++;
            FNR++;

            return true;
        }
    }

    // Constructor for the Interpreter class
    public Interpreter(ProgramNode programNode, String filePath, List<String> lines) {
        this.globalVariables = new HashMap<>();
        this.lineManager = new LineManager(lines);

        // globalVariables HashMap
        globalVariables.put("FILENAME", new InterpreterDataType(filePath));
        globalVariables.put("FS", new InterpreterDataType(" ")); // Set the default value of the FS global variable to “ “ (a single space)
        globalVariables.put("OFS", new InterpreterDataType(" ")); // Set OFS to “ “ (a single space)
        globalVariables.put("ORS", new InterpreterDataType("\n")); // Set ORS to “\n”
        globalVariables.put("OFMT", new InterpreterDataType("%.6g")); // Set OFMT to “%.6g”

        populateFunctionHashMap(programNode);
        populateBuiltInFunctions();
    }

    // Method for the function hash map in the ProgramNode
    private void populateFunctionHashMap(ProgramNode programNode) {
    //
    }

    // BuiltIn functions HashMap
    private void populateBuiltInFunctions() {
    //
    }

    // GETIDT method 
    public InterpreterDataType GetIDT(Node node, HashMap<String, InterpreterDataType> localVariables) {
        if (node instanceof AssignmentNode) {
            // AssignmentNode
            AssignmentNode assignmentNode = (AssignmentNode) node;
            Node left = assignmentNode.getLeft();
            if (left instanceof VariableReferenceNode || (left instanceof OperationNode && ((OperationNode) left).getOperationType() == OperationType.DOLLAR)) {
                InterpreterDataType result = GetIDT(assignmentNode.getRight(), localVariables);
                // Set the target's value to the result
                return result;
            } else {
                // checks if the target is not a variable
                throw new RuntimeException("Invalid target in AssignmentNode");
            }
        
        //ConstantNode - return a new IDT with the value set to the constant node’s value.

        } else if (node instanceof ConstantNode) {
            ConstantNode constantNode = (ConstantNode) node;
            if (constantNode.getStringValue() != null) {
                return new InterpreterDataType(constantNode.getStringValue());
            } else {
                return new InterpreterDataType(constantNode.getNumberValue());
            }
        //FunctionCallNode - 
        } else if (node instanceof FunctionCallNode) {
        
            return new InterpreterDataType(""); //Returns empty strings 

        } 
        //PatternNode - if someone is trying to pass a pattern to a function or an assignment. Throw an exception.

        else if (node instanceof PatternNode) {
            throw new UnsupportedOperationException("Pattern cannot be used for function calls or assignments.");
        }

        //TernaryNode - evaluate the boolean condition (using GetIDT), then evaluate (using GetIDT) and return either the true case or the false case
          else if (node instanceof TernaryNode) {
            TernaryNode ternaryNode = (TernaryNode) node;
            InterpreterDataType conditionResult = GetIDT(ternaryNode.getCondition(), localVariables);
            if (conditionResult.toBoolean()) {
                return GetIDT(ternaryNode.getConsequent(), localVariables);
            } else {
                return GetIDT(ternaryNode.getAlternate(), localVariables);
            }
        }

        //VariableReferenceNode - two cases
        else if (node instanceof VariableReferenceNode) {
            VariableReferenceNode variableReferenceNode = (VariableReferenceNode) node;
            String name = variableReferenceNode.getName();
            Optional<Node> index = variableReferenceNode.getIndex();
        
            if (!index.isPresent()) {
                // Variable reference if there's not an index
                if (localVariables != null && localVariables.containsKey(name)) {
                    return localVariables.get(name);
                } else if (globalVariables.containsKey(name)) {
                    return globalVariables.get(name);
                } else {
                    throw new RuntimeException("Variable not found: " + name);
                }
            } else {
                // Variable reference if theres an index
                Node indexNode = index.get();
                InterpreterDataType indexResult = GetIDT(indexNode, localVariables);
        

                // Array reference - resolve the index then look the index up in the variable’s hash map. If the variable is not an IADT, throw an exception.

                if (globalVariables.containsKey(name) && globalVariables.get(name) instanceof InterpreterArrayDataType) {
                    InterpreterArrayDataType arrayData = (InterpreterArrayDataType) globalVariables.get(name);
                    if (arrayData.hasValue(indexResult)) {
                        return arrayData.getValue(indexResult);
                    } else {
                        throw new RuntimeException("Index not found in the array: " + name);
                    }
                } else {
                    throw new RuntimeException("Variable is not an array: " + name);
                }
            }
        
        } 
        //OperationNode - Evaluate the left and right (if there is one) using GetIDT. 
        else if (node instanceof OperationNode) {
            OperationNode operationNode = (OperationNode) node;
            Node left = operationNode.getLeft();
            Optional<Node> optionalRight = operationNode.getRight();
        
            InterpreterDataType leftResult = GetIDT(left, localVariables);
            InterpreterDataType rightResult = null;
        
            if (optionalRight.isPresent()) {
                Node right = optionalRight.get();
                rightResult = GetIDT(right, localVariables);
            }
        
            switch (operationNode.getOperationType()) {
                // Math-based operations: converts to float, does the operation then converts to string to store in a new IDT. 

                case ADD:
                    return new InterpreterDataType(String.valueOf(Float.parseFloat(leftResult.toString()) + Float.parseFloat(rightResult.toString())));
                case SUBTRACT:
                    return new InterpreterDataType(String.valueOf(Float.parseFloat(leftResult.toString()) - Float.parseFloat(rightResult.toString())));
                case MULTIPLY:
                    return new InterpreterDataType(String.valueOf(Float.parseFloat(leftResult.toString()) * Float.parseFloat(rightResult.toString())));
                case DIVIDE:
                    return new InterpreterDataType(String.valueOf(Float.parseFloat(leftResult.toString()) / Float.parseFloat(rightResult.toString())));
                case MODULO:
                    return new InterpreterDataType(String.valueOf(Float.parseFloat(leftResult.toString()) % Float.parseFloat(rightResult.toString())));
                case EXPONENT:
                    return new InterpreterDataType(String.valueOf(Math.pow(Float.parseFloat(leftResult.toString()), Float.parseFloat(rightResult.toString()))));
                default:
                    throw new UnsupportedOperationException("This is not an operation: " + operationNode.getOperationType());
            }
        }        
        return null; 
    }

    //CompareValues
    private InterpreterDataType compareValues(InterpreterDataType left, InterpreterDataType right, OperationType operationType ) {
       
        // For compares (equal, not equal, etc), compare as floats if both sides covert to float
        if (left.isFloat() && right.isFloat()) {
            double leftValue = left.toFloat();
            double rightValue = right.toFloat();
    
            switch (operationType) {
                case EQ:
                    return new InterpreterDataType(leftValue == rightValue ? 1 : 0); //Compares left and right value for Equals(==)
                case NE:
                    return new InterpreterDataType(leftValue != rightValue ? 1 : 0); //Compares left and right value for Not Equals(!=)
                case LT:
                    return new InterpreterDataType(leftValue < rightValue ? 1 : 0); //Compares the left and right value for Less Than(<)
                case LE:
                    return new InterpreterDataType(leftValue <= rightValue ? 1 : 0); //Compares the left and right value for Less Than or Equal to(<=)
                case GT:
                    return new InterpreterDataType(leftValue > rightValue ? 1 : 0); //Compares the left and right value for Greater Than (>)
                case GE:
                    return new InterpreterDataType(leftValue >= rightValue ? 1 : 0); //Compare the left and right value for Greater Than or Equal to(>=)
                default:
                    throw new UnsupportedOperationException("This is not a valid comparison type: " + operationType); //error message if the user inputs an incorrect comparison type 
            }
        } else {
            // This comparison type is similar to the other one but instead it deals with string comparisons
            String leftValue = left.toString();
            String rightValue = right.toString();
    
            switch (operationType) {
                case EQ:
                    return new InterpreterDataType(leftValue.equals(rightValue) ? 1 : 0);//Compares to left and right value to see if they're equal
                case NE:
                    return new InterpreterDataType(!leftValue.equals(rightValue) ? 1 : 0);//Compares the left and right value to see if they're not equal
                case LT:
                    return new InterpreterDataType(leftValue.compareTo(rightValue) < 0 ? 1 : 0); //Compares the left value to the right to see if its less
                case LE:
                    return new InterpreterDataType(leftValue.compareTo(rightValue) <= 0 ? 1 : 0); //Compares the left value to the right to see if its less than or equal to it
                case GT:
                    return new InterpreterDataType(leftValue.compareTo(rightValue) > 0 ? 1 : 0); //Compares the left value to the right to see if its greater
                case GE:
                    return new InterpreterDataType(leftValue.compareTo(rightValue) >= 0 ? 1 : 0); //Compares the left value to the right to see if its greater than or equal to
                default:
                    throw new UnsupportedOperationException("This is not a valid comparison type: " + operationType); //Error message if the input is not a correct comparison type(based on the comparison types we have above)
            }
        }
    }

    //Boolean operations (and, or, not)
    private InterpreterDataType BooleanOperation(InterpreterDataType left, InterpreterDataType right, OperationType operationType) {
        boolean leftBoolean = left.toBoolean();
        boolean rightBoolean = right.toBoolean();
    
        switch (operationType) {
            case AND:
                return new InterpreterDataType(leftBoolean && rightBoolean ? 1 : 0); // Checks to see if both(&&) the left and right values are true. Returns 1 if they are, and 0 if they are not
            case OR:
                return new InterpreterDataType(leftBoolean || rightBoolean ? 1 : 0); // Checks if either(||) the left or right value is true and returns 1 if at least one of them is, and 0 if they are not
            case NOT:
                return new InterpreterDataType(!leftBoolean ? 1 : 0); //Checks if the left value is false and returns 1 if it is, and 0 if it is not
            default:
                throw new UnsupportedOperationException("Not a boolean operation type " + operationType);//Error message if the operation given isnt listen above
        }
    }
    //Match
    private InterpreterDataType Match(String left, String right, ComparisonType comparisonType) {
        // Use Java's built-in Regex object to deal with match and not match operations
        boolean isMatch;
        if (comparisonType == ComparisonType.EQUAL) {
            isMatch = left.matches(right);
        } else if (comparisonType == ComparisonType.NOT_EQUAL) {
            isMatch = !left.matches(right);
        } else {
            throw new UnsupportedOperationException("This isnt a comparison type: " + comparisonType);
        }
    
        return new InterpreterDataType(isMatch ? 1 : 0);
    }
//notMatch
private InterpreterDataType notMatch(InterpreterDataType input, PatternNode pattern) {
    boolean isNotMatch = !left.matches(right);

    return new InterpreterDataType(isNotMatch ? 1 : 0);
}

//DollarOperation - evaluates the left side, adds a “$”, then gets that as a variable. 
private InterpreterDataType DollarOperation(Node node, HashMap<String, InterpreterDataType> localVariables) {
    InterpreterDataType evaluatedValue = GetIDT(node, localVariables);
    return localVariables.get("$" + evaluatedValue.toString());
}

//IncrementDecrement - Similar to the math-based operations

private InterpreterDataType IncrementDecrement(InterpreterDataType operand, OperationType operationType) {
    double value = operand.toDouble();
    switch (operationType) {
        case PREINC:
            return new InterpreterDataType(++value); //PREINC - Before returning it increments the value by 1
        case POSTINC:
            return new InterpreterDataType(value++); //POSTINC - After returning it increments the value by 1
        case PREDEC:
            return new InterpreterDataType(--value); //PREDEC -  Before returing it decrements the value by 1 
        case POSTDEC:
            return new InterpreterDataType(value--); //POSTDEC - After returning it decrements the value by 1 
        case UNARYPOS:
            return new InterpreterDataType(+value); //UNARYPOS - returns the positive value
        case UNARYNEG:
            return new InterpreterDataType(-value); //UNARYNEG - returns the negative value
        default:
            throw new UnsupportedOperationException("Not a listed operation type  " + operationType); //Error message if the opartion type isnt in the list above 
    }
}

//Concatenation - A simple string concatenation of the left and right values

private InterpreterDataType Concatenation(InterpreterDataType left, InterpreterDataType right) {
    String leftValue = left.toString();
    String rightValue = right.toString();
    return new InterpreterDataType(leftValue + rightValue);
}

//In - Check to make sure the right hand side is a variable reference and is an array

private InterpreterDataType In(InterpreterDataType left, InterpreterDataType right, HashMap<String, InterpreterDataType> localVariables, HashMap<String, InterpreterDataType> globalVariables) {
    if (!right.isArray()) {
        throw new UnsupportedOperationException("right hand side is not an array.");//Throws the exception if its not an array
    }
// look up the left hand side in the array (which will be in globals or locals)
    String leftValue = left.toString();
    if (right.getArrayValue().containsKey(leftValue)) {
        return new InterpreterDataType(1);
    } else {
        return new InterpreterDataType(0);
    }
}
//
//
//
//
//
//
//
public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> locals, StatementNode stmt) {
//StatementNode
    if (stmt instanceof AssignmentNode) {
        AssignmentNode assignmentNode = (AssignmentNode) stmt;
        Node left = assignmentNode.getLeft();
        Node right = assignmentNode.getRight();

        // Evaluate the right side using GetIDT
        InterpreterDataType rightResult = GetIDT(right, locals);

        // Evaluate the left side using GetIDT
        InterpreterDataType leftResult = GetIDT(left, locals);

        // Set left's value equal to the result of GetIDT(right)
        if (left instanceof VariableReferenceNode || (left instanceof OperationNode && ((OperationNode) left).getOperationType() == OperationType.DOLLAR)) {
            // VariableReferenceNode and OperationNode(DOLLAR) from the left
            String variableName = left.toString();
            if (locals != null && locals.containsKey(variableName)) {
                locals.put(variableName, rightResult);
            } else if (globalVariables.containsKey(variableName)) {
                globalVariables.put(variableName, rightResult);
            } 
        } 
        // Returns the enum type None, and the value of right

        return new ReturnType(ReturnTypeEnum.NONE, rightResult.toString());
    }
    
//BreakNode 
    else if (stmt instanceof BreakNode) {
        //BreakNode - return with a return type of break
        return new ReturnType(ReturnTypeEnum.BREAK, "break");
    }

//ContinueNode
    else if (stmt instanceof ContinueNode) {
        //ContinueNode - return with a return type of Continue
        return new ReturnType(ReturnTypeEnum.CONTINUE, "continue");
    }

//DeleteNode
    else if (stmt instanceof DeleteNode) {
        //gets the array from the local and gloabal variables 
            DeleteNode deleteNode = (DeleteNode) stmt;
            Node target = deleteNode.getTarget();
            Node indices = deleteNode.getIndices();
            InterpreterArrayDataType arrayData = getArrayData(target, locals, globalVariables);
    
            //If indices is set, delete them from the array, otherwise delete them all
            if (indices != null) {
                // Evaluate indices using GetIDT
                InterpreterDataType indicesResult = GetIDT(indices, locals);
                
                // Delete specific indices from the array
                deleteIndicesFromArray(arrayData, indicesResult);
            } else {
                // clear deletes all the elements from the array
                arrayData.clear();
            }
    
            // Return type of None
            return new ReturnType(ReturnTypeEnum.NONE);
        }

// DoWhileNode
    else if (stmt instanceof DoWhileNode) {
        DoWhileNode doWhileNode = (DoWhileNode) stmt;
        //gets the condition node as well as the list of statements from the DoWhileNode
        Node conditionNode = doWhileNode.getCondition();
        List<StatementNode> statements = doWhileNode.getStatements();

        // uses GETIDT to valuate the condition 
        InterpreterDataType conditionResult;

        do {
            // Interprets the list of statements
            ReturnType result = InterpretListOfStatements(locals, statements);

            // Check if the result is Break
            if (result.getType() == ReturnTypeEnum.BREAK) {
                break; // Break out of the loop
            }

            // Evaluate the condition for the next iteration
            conditionResult = GetIDT(conditionNode, locals);

        } while (conditionResult.toBoolean());

        // Return type None
        return new ReturnType(ReturnTypeEnum.NONE);
    }

//ForNode
else if (stmt instanceof ForNode) {
    ForNode forNode = (ForNode) stmt;

    // If there is an initial, call processStatement on it
    if (forNode.getInitialization() != null) {
        ProcessStatement(locals, forNode.getInitialization());
    }

    // Create a while loop, using the forNode’s condition as the while’s condition
    while (GetIDT(forNode.getCondition(), locals).getBooleanValue()) {
        // Inside, call InterpretListOfStatements() on forNode’s statements
        ReturnType result = InterpretListOfStatements(forNode.getStatements(), locals);

        // Same as DoWhile – check the return code and do the same thing
        if (result.getType() == ReturnTypeEnum.BREAK) {
            break;
        }
        
        //calls processStatement() on the forNode’s increment
        ProcessStatement(locals, forNode.getIncrement());
    }

    // Return type None
    return new ReturnType(ReturnTypeEnum.NONE);
}

// ForEachNode
else if (stmt instanceof ForEachNode) {

    ForEachNode forEachNode = (ForEachNode) stmt;

    // Finds the array
    InterpreterDataType array = GetIDT(forEachNode.getArray(), locals);

    // Loop over every key in the array’s hashMap
    for (String key : ((InterpreterArrayDataType) array).keySet()) {
        // Set the variable to the key
        locals.put(forEachNode.getVariable(), new InterpreterDataType(key));

        // Call InterpretListOfStatements on the forEach’s statements
        ReturnType result = InterpretListOfStatements(forEachNode.getStatements(), locals);

        // Follow the same return rules as doWhile
        if (result.getType() == ReturnTypeEnum.BREAK) {
            return result;
        }
    }

    // Return type None
    return new ReturnType(ReturnTypeEnum.NONE);
}

//FunctionCallNode
else if (stmt instanceof FunctionCallNode) {
    FunctionCallNode functionCallNode = (FunctionCallNode) stmt;

    // Call RunFunctionCall()
    RunFunctionCall(functionCallNode, locals);

    // Return the enum value type None
    return new ReturnType(ReturnTypeEnum.NONE);
}

//IfNode
else if (stmt instanceof IfNode) {
 
    IfNode currentIfNode = (IfNode) stmt;

    // Go through the linked list of IfNodes
    while (currentIfNode != null) {
        // Looks for an IfNode where Condition is empty OR it evaluates to true
        boolean conditionIsTrue = currentIfNode.getCondition() == null || GetIDT(locals, currentIfNode.getCondition()).toBoolean();

        if (conditionIsTrue) {
            // Call InterpretListOfStatements on currentIfNode.statements
            ReturnType result = InterpretListOfStatements(currentIfNode.getStatements(), locals);

            // If the return from InterpretListOfStatements is not the enum value of "None", then return, passing that result back to the caller
            if (result.getType() != ReturnTypeEnum.NONE) {
                return result;
            }
        }

        // Moves to the next IfNode in the linked list
        currentIfNode = currentIfNode.getNextIfNode();
    }

}

//ReturnNode
else if (stmt instanceof ReturnNode) {

    ReturnNode returnNode = (ReturnNode) stmt;
    //if there is a value then we evaluate it 
    if (returnNode.hasValue()) {
        // Evaluate the return value with GetIDT
        InterpreterDataType returnValue = GetIDT(returnNode.getValue(), locals);

        // creates a ReturnType with the value and the enum type Return
        return new ReturnType(ReturnTypeEnum.RETURN, returnValue.getValue());
    } else {
        // If there is no value, we make a ReturnType with the enum type Return 
        return new ReturnType(ReturnTypeEnum.RETURN);
    }
}
// WhileNode  
//Note - (Its the same as the DoWhileNode but with a while loop instead)
else if (stmt instanceof WhileNode) {
    WhileNode whileNode = (WhileNode) stmt;
    // Gets the condition node as well as the list of statements from the WhileNode
    Node conditionNode = whileNode.getCondition();
    List<StatementNode> statements = whileNode.getStatements();

    // Uses GETIDT to evaluate the condition
    InterpreterDataType conditionResult;
    //We're changing the dowhile loop to a while loop 
    while ((conditionResult = GetIDT(conditionNode, locals)).toBoolean()) {
        // Interprets the list of statements
        ReturnType result = InterpretListOfStatements(locals, statements);

        // Check if the result is Break
        if (result.getType() == ReturnTypeEnum.BREAK) {
            // Break  - leaves the loop
            break;
        }
    }

    // Return the enum type None
    return new ReturnType(ReturnTypeEnum.NONE);
}
}
//
//
//
//
//
//
//InterpretListOfStatements
public ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, HashMap<String, InterpreterDataType> locals) {
    ReturnType result = new ReturnType(ReturnTypeEnum.NONE);

    for (StatementNode statement : statements) {
        result = ProcessStatement(locals, statement);

        // Check if the result is not None
        if (result.getType() != ReturnTypeEnum.NONE) {
            return result; // return passing “up” the same ReturnType.
        }
    }

    return result;

}
public void InterpretProgram(ProgramNode programNode) {
    for (BlockNode block : programNode.getBlocks()) {
        if (block.getType() == BlockType.BEGIN) {
            InterpretBlock(block);
            lineManager.callSplitAndAssign(); // Calls SplitAndAssign after processing BEGIN block

            // Process records and InterpretBlock for non-BEGIN and non-END blocks
            for (String record : lineManager.getLines()) {
                for (BlockNode innerBlock : programNode.getBlocks()) {
                    if (innerBlock.getType() != BlockType.BEGIN && innerBlock.getType() != BlockType.END) {
                        InterpretBlock(innerBlock);
                    }
                }
            }
        }
    }

    // Process END blocks
    for (BlockNode block : programNode.getBlocks()) {
        if (block.getType() == BlockType.END) {
            InterpretBlock(block);
        }
    }
}
    private void InterpretBlock(BlockNode block) {
        // Looks to see if the block has a condition
        Node conditionNode = block.getCondition();
        if (conditionNode == null || GetIDT(conditionNode, new HashMap<>()).toBoolean()) {
            // Iterate through the statement in block then call ProcessStatement
            for (StatementNode statement : block.getStatements()) {
                ProcessStatement(new HashMap<>(), statement);
            }
        }
    }

//RunFunctionCall - method that takes the function call node and locals and returns a String. 
   private void RunFunctionCall(HashMap<String, InterpreterDataType> locals, FunctionCallNode functionCallNode) {
    String functionName = functionCallNode.getFunctionName();
    FunctionDefinition func = findFunction(functionName);

    // Check if the function exists
    if (func == null) {
        throw new RuntimeException("function doesn’t exist " + functionName);
    }

    // Checks if parameter counts match for non-variadic functions
    if (!func.isVariadic() && func.getParameterCount() != functionCallNode.getParameterCount()) {
        throw new RuntimeException("parameter counts don’t match up on non-variadics: " + functionName);
    }

    // makes a map for function parameters
    HashMap<String, InterpreterDataType> paramMap = new HashMap<>();
    int parameterIndex = 0;

    // Maps the  parameters 
    for (Parameter parameter : func.getParameters()) {
        String paramName = parameter.getName();

        // Check if function call Node has the correct amount of parameters
        if (parameterIndex >= functionCallNode.getParameterCount()) {
            throw new RuntimeException("parameter counts don’t match up on variadics: " + functionName);
        }

        // Map the parameter to its value
        Node paramNode = functionCallNode.getParameters().get(parameterIndex);
        InterpreterDataType paramValue = getIDT(paramNode, locals);
        paramMap.put(paramName, paramValue);

        parameterIndex++;
    }

    // If the function is variadic, create an array for the rest of the parameters
    if (func.isVariadic()) {
        InterpreterArrayDataType variadicArray = new InterpreterArrayDataType();
        while (parameterIndex < functionCallNode.getParameterCount()) {
            Node paramNode = functionCallNode.getParameters().get(parameterIndex);
            InterpreterDataType paramValue = getIDT(paramNode, locals);
            variadicArray.addValue(paramValue);
            parameterIndex++;
        }
        paramMap.put(func.getVariadicParameterName(), variadicArray);
    }

    // Executes the function 
    if (func.isBuiltin()) {
        func.execute(paramMap);
    } else {
        InterpretListOfStatements(func.getStatements(), paramMap);
    }
}

    private InterpreterDataType getIDT(Node paramNode, HashMap<String, InterpreterDataType> locals) {
    return null;
}
    }
    

