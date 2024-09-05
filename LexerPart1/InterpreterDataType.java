package LexerPart1;

import java.util.HashMap;

// Interpreter Data Type (IDT) class
// Holds a string and has two constructors – one with and one without an initial value supplied
class InterpreterDataType {
    private String value;

    public InterpreterDataType() {
        this.value = "";
    }

    public InterpreterDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean toBoolean() {
        return false;
    }

    public boolean getBooleanValue() {
        return false;
    }

}

// Interpreter Array Data Type class
// Holds a HashMap<String,IDT>, and Its constructor creates that hash map.
class InterpreterArrayDataType extends HashMap<String, InterpreterDataType> {
    public InterpreterArrayDataType() {
        super();
    }
        //setter for the value of the key
        public void setValueForKey(String key, InterpreterDataType value) {
            this.put(key, value);
        }
    
        // Getter for the value for the key, it also returns null if key does not exist
        public InterpreterDataType getValueForKey(String key) {
            return this.get(key);
        }
    }
