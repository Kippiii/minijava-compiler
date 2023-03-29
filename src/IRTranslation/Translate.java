package IRTranslation;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;

import java.io.FileNotFoundException;

public class Translate {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            translate(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static void translate(String filename) throws FileNotFoundException, CompilerExceptionList {
        translate(filename, false);
    }

    public static void translate(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        // TODO
    }
}
