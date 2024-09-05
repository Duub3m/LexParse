package LexerPart1;

//char Peek(i) -looks “i” characters ahead and returns that character; doesn’t move the index
//String PeekString(i) – returns a string of the next “i” characters but doesn’t move the index
//char GetChar() – returns the next character and moves the index
//void Swallow(i) – moves the index ahead “i” positions
//boolean IsDone() – returns true if we are at the end of the document
//String Remainder() – returns the rest of the document as a string


//STEP 1
public class StringHandler {
    private String awkFile;
    private int index;

    public StringHandler(String awkFile) {
        this.awkFile = awkFile;
        this.index = 0; // Index at 0 corresponds to the start of the file.
    }

    public char Peek(int i) { // char Peek(i) - looks "i" characters ahead and returns that character; doesn't move the index
        if (index + i < awkFile.length()) {
            return awkFile.charAt(index + i);
        } else {
            return ' '; // If a space is seen it returns a space 
    }
}

    public String PeekString(int i) { // String PeekString(i) – returns a string of the next "i" characters but doesn't move the index
        if (index + i <= awkFile.length()) {
            return awkFile.substring(index, index + i);
        } else {
            return ""; // Returns an empty string 
        }
    }

    public char GetChar() { // char GetChar() – returns the next character and moves the index
        if (!IsDone()) {
            char currentChar = awkFile.charAt(index);
            index++;
            return currentChar;
        } else {
            return ' '; // Returns a space 
        }
    }

    public void Swallow(int i) { //void Swallow(i) – moves the index ahead “i” positions
        index += i;
    }

    public boolean IsDone() { //boolean IsDone() – returns true if we are at the end of the document
        return index >= awkFile.length();
    }

    public String Remainder() { //String Remainder() – returns the rest of the document as a string
        return awkFile.substring(index);
    }
}
