package LexerPart1;

public class AssignmentNode extends Node {
    private Node left; //target variable
    private Node right; //expression that we use for the target 

    public AssignmentNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "AssignmentNode(target: " + left + ", expression: " + right + ")";
    }
}
