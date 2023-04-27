package AbstractSyntax;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ParserGenerator.MiniJavaParser;
import ParserGenerator.ParseException;
import Parsing.Parse;
import syntax.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BuildAbstract {
    /**
     * The driver code for the build abstract syntax tree phase of the compiler
     * @param filename - The source file path for the compiler
     */
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * Ran by when the main phase of the compiler is the build abstract syntax tree phase
         * @throws FileNotFoundException
         */
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
        /**
         * Builds the abstract syntax tree associated with the source program
         * @param filename - The source file path for the compiler
         * @return The abstract syntax tree
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         */
        return buildAbstractTree(filename, false);
    }

    public static Program buildAbstractTree(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        /**
         * Builds the abstract syntax tree associated with the source program
         * @param filename - The source file path for the compiler
         * @param debug - Whether to print debug output
         * @return The abstract syntax tree
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         */
        Parse.parse(filename);

        final MiniJavaParser parser = new MiniJavaParser(new FileInputStream(filename));
        parser.disable_tracing();

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
