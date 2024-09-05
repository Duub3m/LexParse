package LexerPart1;

import java.util.Optional;

public class OperationNode extends Node {
    private Node left;
    private Optional<Node> right;
    private OperationType operationType;
    
// Operation node has a Node left, an Optional<Node> right and a list of possible operations 
    public OperationNode(Node left, Optional<Node> right, OperationType operationType) {
        this.left = left;
        this.right = right;
        this.operationType = operationType;
    }

    public Node getLeft() {
        return left;
    }

    public Optional<Node> getRight() {
        return right;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }

}


// Enum for different types of operations(Used in Parser.java)
enum OperationType {
    EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR,
    PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS, UNARYNEG, IN,
    EXPONENT, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, CONCATENATION, LOGICAL_AND, LOGICAL_OR
}



