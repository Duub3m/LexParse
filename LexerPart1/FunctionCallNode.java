package LexerPart1;

import java.util.HashMap;
import java.util.List;

public class FunctionCallNode {
    private String functionName;
    private List<ArgumentNode> arguments;

    public FunctionCallNode(String functionName, List<ArgumentNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ArgumentNode> getArguments() {
        return arguments;
    }

    public HashMap<String, InterpreterDataType> getParameters() {
        return null;
    }

    public Object getParameterCount() {
        return null;
    }
}

