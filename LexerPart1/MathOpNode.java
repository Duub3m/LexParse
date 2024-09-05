package LexerPart1;

//MathOpNode
//Handles operations with left and right operands and an operation type.
public class MathOpNode extends Node {
    private Node left;
    private Node right;
    private OperationType operation; 

    public MathOpNode(Node left, OperationType operation, Node right) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public OperationType getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "MathOpNode(left: " + left + ", operation: " + operation + ", right: " + right + ")";
    }
}
