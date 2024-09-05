package LexerPart1;

//TernaryNode
//Conditional expression with a condition, consequent, and alternate expression.

public class TernaryNode extends Node {
    private Node condition;
    private Node consequent;
    private Node alternate;

    public TernaryNode(Node condition, Node consequent, Node alternate) {
        this.condition = condition;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getConsequent() {
        return consequent;
    }

    public Node getAlternate() {
        return alternate;
    }

    @Override
    public String toString() {
        return "TernaryNode{" + "condition=" + condition + ", consequent=" + consequent +", alternate=" + alternate +'}';
    }
}
