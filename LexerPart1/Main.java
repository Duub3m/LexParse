package LexerPart1;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Reads through the AWK file

            Path myPath = Paths.get("/Users/dubemeneh/Desktop/awkFile.awk");
            String fileContent = new String(Files.readAllBytes(myPath));

            // Makes the Lexer
            Lexer lexer = new Lexer(fileContent);

            // Lex's through the file and gets the list of tokens
            List<Token> tokens = lexer.Lex();

            // Goes through the tokens and then prints them out
            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("Error =: " + e.getMessage());
            System.exit(1);
        }
    }
}
