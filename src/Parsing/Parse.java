package Parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ParserGenerator.*;
import LexicalAnalysis.Scan;
import LexicalAnalysis.ManyLexException;

public class Parse {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        try {
            Scan.scan(filename);
        } catch (ManyLexException e) {
            // TODO
            return;
        }

        parse(filename, debug);
    }

    public static void parse(String filename) throws FileNotFoundException {
        parse(filename, false);
    }

    public static void parse(String filename, boolean debug) throws FileNotFoundException {
        try {
            Scan.scan(filename);
        } catch (ManyLexException e) {
            // TODO
            return;
        }

        final MiniJavaParser lexer = new MiniJavaParser(new FileInputStream(filename));

        if (debug)
            lexer.enable_tracing();
        else
            lexer.disable_tracing();

        try {
            lexer.Program();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
