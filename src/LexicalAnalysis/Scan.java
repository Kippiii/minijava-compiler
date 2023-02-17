package LexicalAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ParserGenerator.*;

public class Scan {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 1)
            filename = "Factorial.java";
        else
            filename = args[0];

        boolean debug = args.length >= 2 ? args[1].equals("debug") : false;

        int errors = 0;

        try {
            scan(filename, debug);
        } catch (CompilerExceptionList e) {
            for (CompilerException e2 : e) {
                System.err.printf("%s:%s%n", filename, e2.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static void scan(String file) throws CompilerExceptionList, FileNotFoundException {
        scan(file, false);
    }

    public static void scan(String file, boolean debug) throws CompilerExceptionList, FileNotFoundException {
        final MiniJavaParser lexer = new MiniJavaParser(new FileInputStream(file));

        if (debug)
            lexer.enable_tracing();
        else
            lexer.disable_tracing();

        List<CompilerException> errors = new ArrayList<CompilerException>();
        while (true) {
            final Token token = lexer.getNextToken();
            if (token.kind == MiniJavaParserConstants.MONKEY) {
                errors.add(new LexException(token));
            } else {
                if (token.kind == MiniJavaParserConstants.EOF)
                    break;
            }
        }

        if (errors.size() > 0)
            throw new CompilerExceptionList(errors);
    }

}
