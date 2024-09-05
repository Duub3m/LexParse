package LexerPart1;

import java.util.LinkedList;
import java.util.Optional;

public abstract class Node {
    public abstract String toString();

}

class ProgramNode extends Node {
    @Override
    public String toString() {
        return "ProgramNode";
    }

    public BlockNode[] getBlocks() {
        return null;
    }
}

class FunctionDefinitionNode extends Node {
    public FunctionDefinitionNode(String name, LinkedList<String> parameters) {
    }

    @Override
    public String toString() {
        return "FunctionDefinitionNode";
    }
}

class BlockNode extends Node {
    @Override
    public String toString() {
        return "BlockNode";
    }

    public Node getCondition() {
        return null;
    }

    public LexerPart1.Parser.StatementNode[] getStatements() {
        return null;
    }

    public Object getType() {
        return null;
    }
}



class ConstantNode extends Node {
    private String stringValue;
    private double numberValue; 

    public ConstantNode(String value) {
        this.stringValue = value;
    }

    public ConstantNode(double value) {
        this.numberValue = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public double getNumberValue() {
        return numberValue;
    }

    @Override
    public String toString() {
        return "ConstantNode(value: " + (stringValue != null ? stringValue : numberValue) + ")";
    }
}

class PatternNode extends Node {
    private String patternValue;

    public PatternNode(String value) {
        this.patternValue = value;
    }

    public String getPatternValue() {
        return patternValue;
    }

    @Override
    public String toString() {
        return "PatternNode(value: " + patternValue + ")";
    }
}

//StatementNode Class
    class StatementNode extends Node {
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

    // Variable Refrence Node class
    class VariableReferenceNode extends Node {
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
        return condition;
    }
    public LinkedList<StatementNode> getStatements() {
        return null;
    }
    public IfNode getNextIfNode() {
        return null;
    }

}


    





