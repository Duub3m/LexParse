package LexerPart1;
import java.util.HashMap;
import java.util.LinkedList;

class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode {
    private boolean isVariadic;
    private BuiltInFunction execute;

    public BuiltInFunctionDefinitionNode(String name, LinkedList<String> parameters, boolean isVariadic, BuiltInFunction execute) {
        super(name, parameters);
        this.isVariadic = isVariadic;
        this.execute = execute;
    }

    public boolean isVariadic() {
        return isVariadic;
    }

    public String execute(HashMap<String, InterpreterDataType> parameters) {
        return execute.Execute(parameters);
    }
}

interface BuiltInFunction {
    String Execute(HashMap<String, InterpreterDataType> parameters);


// Instances of BuiltInFunctionDefinitionNode for the "print" and "printf" functions

//built-in function for "print"
BuiltInFunctionDefinitionNode printFunction = new BuiltInFunctionDefinitionNode( "print", new LinkedList<>(),
        true, //Mark "print" as variadic (True)
        (HashMap<String, InterpreterDataType> parameters) -> {
            StringBuilder result = new StringBuilder();
            for (InterpreterDataType value : parameters.values()) {
                result.append(value.toString()).append(" ");
            }
            System.out.print(result);
            return "";
        }
);
////built-in functions for "printf"
BuiltInFunctionDefinitionNode printfFunction = new BuiltInFunctionDefinitionNode("printf", new LinkedList<>(),
  true, ////Mark "printf" as variadic (True)
        (HashMap<String, InterpreterDataType> parameters) -> {
            String format = parameters.get("0").toString();
            Object[] args = parameters.values().stream().map(Object::toString).toArray();
            System.out.printf(format, args);
            return "";
        }
);
// Built-in functions for "getline"
BuiltInFunctionDefinitionNode getlineFunction = new BuiltInFunctionDefinitionNode("getline", new LinkedList<>(), false,
        (HashMap<String, InterpreterDataType> parameters) -> {
            //split assign 
            return "1";
        }
);

// Built-in functions for "next"
BuiltInFunctionDefinitionNode nextFunction = new BuiltInFunctionDefinitionNode("next", new LinkedList<>(), false,
        (HashMap<String, InterpreterDataType> parameters) -> { 
            //split assign 
            return "0";
        }
);

// Built-in functions for gsub, match, sub
BuiltInFunctionDefinitionNode gsubFunction = new BuiltInFunctionDefinitionNode("gsub", new LinkedList<>(), true,
        parameters -> {
            String input = parameters.get("$0").toString();
            String regex = parameters.get("$1").toString();
            String replacement = parameters.get("$2").toString();
            String result = input.replaceAll(regex, replacement);
            return result;
        }
);

BuiltInFunctionDefinitionNode matchFunction = new BuiltInFunctionDefinitionNode("match", new LinkedList<>(), false,
        parameters -> {
            String input = parameters.get("$0").toString();
            String regex = parameters.get("$1").toString();
            boolean isMatch = input.matches(regex);
            return isMatch ? "1" : "0";
        }
);

BuiltInFunctionDefinitionNode subFunction = new BuiltInFunctionDefinitionNode("sub", new LinkedList<>(), false,
        parameters -> {
            String input = parameters.get("$0").toString();
            String regex = parameters.get("$1").toString();
            String replacement = parameters.get("$2").toString();
            return input.replaceAll(regex, replacement);
        }
);
}