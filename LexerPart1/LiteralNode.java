package LexerPart1;

public class LiteralNode extends Node {
    private Object value; 
    
    public LiteralNode(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
