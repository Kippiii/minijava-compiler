package Parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ParserGenerator.*;
import LexicalAnalysis.Scan;

public class Parse {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            parse(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static void parse(String filename) throws FileNotFoundException, CompilerExceptionList {
        parse(filename, false);
    }

    public static void parse(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        Scan.scan(filename);

        final MiniJavaParser lexer = new MiniJavaParser(new FileInputStream(filename));
        MiniJavaParser.init_errors();

        if (debug)
            lexer.enable_tracing();
        else
            lexer.disable_tracing();

        try {
            lexer.Program();
        } catch (ParseException exc) {
            // Will never happen!
            exc.printStackTrace();
            return;
        }

        List<CompilerException> errors = new ArrayList<CompilerException>();
        for (ParseException exc : MiniJavaParser.errors) {
            errors.add(new MyParseException(exc));
        }

        if (errors.size() > 0) {
            throw new CompilerExceptionList(errors);
        }
    }
}
