package AbstractSyntax;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ParserGenerator.MiniJavaParser;
import ParserGenerator.ParseException;
import Parsing.Parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BuildAbstract {
    static String filename = "Factorial.java";

    public static void main(String[] args) {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            buildAbstractTree(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static Program buildAbstractTree(String filename) throws FileNotFoundException, CompilerExceptionList {
        return buildAbstractTree(filename, false);
    }

    public static Program buildAbstractTree(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        Parse.parse(filename);

        final MiniJavaParser parser = new MiniJavaParser(new FileInputStream(filename));

        Program program;
        try {
            program = parser.Program();
        } catch (ParseException exc) {
            // Will never happen!
            exc.printStackTrace();
            return null;
        }

        if (debug) {
            TreePrinter tp = new TreePrinter();
            tp.visit(program);
        }

        return program;
    }

}
