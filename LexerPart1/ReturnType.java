package LexerPart1;

// Enums for the different return types
enum ReturnTypeEnum {
    NONE,
    NORMAL,
    BREAK,
    CONTINUE,
    RETURN
}

// ReturnType 
class ReturnType {
    private ReturnTypeEnum type;
    private String stringValue;

    // enum constructor
    public ReturnType(ReturnTypeEnum type) {
        this.type = type;
    }

    // enum + string constructor 
    public ReturnType(ReturnTypeEnum type, String stringValue) {
        this.type = type;
        this.stringValue = stringValue;
    }

    // Getter for type
    public ReturnTypeEnum getType() {
        return type;
    }

    // Getter for string value
    public String getStringValue() {
        return stringValue;
    }

    // ToString 
    @Override
public String toString() {
    return (type == ReturnTypeEnum.NONE) ? "Type: " + type : "Type: " + type + ", Value: \"" + stringValue + "\"";
}
}
