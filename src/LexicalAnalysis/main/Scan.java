import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Scan {

    public static void main(String[] args) throws FileNotFoundException {
        final String filename = "Factorial.java";
        final boolean trace = true;

        final MiniJavaParser lexer = new MiniJavaParser (new FileInputStream(filename));
        if (trace)
            lexer.enable_tracing();
        else
            lexer.disable_tracing();

        ArrayList<Token> tokens = new ArrayList<Token>();
        while (true) {
            try {
                final Token token = lexer.getNextToken();
                tokens.add(token);
                if (token.kind == MiniJavaParserConstants.EOF)
                    break;
            } catch (TokenMgrError e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
