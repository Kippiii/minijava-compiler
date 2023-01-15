import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Scan {

    public static void main(String[] args) throws FileNotFoundException {
        final String filename = "test.java";
        final boolean trace = false;

        final MiniJavaParser lexer = new MiniJavaParser (new FileInputStream(filename));
        if (trace)
            lexer.enable_tracing();
        else
            lexer.disable_tracing();

        // TODO
    }

}
