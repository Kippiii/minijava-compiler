package Parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ParserGenerator.*;
import LexicalAnalysis.Scan;
import LexicalAnalysis.ManyParseException;

public class Parse {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length >= 1)
            filename = args[0];

        try {
            Scan.scan(filename);
        } catch (ManyParseException e) {
            // TODO
            return;
        }

        final MiniJavaParser lexer = new MiniJavaParser (new FileInputStream(filename));
        try {
            lexer.Program();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
