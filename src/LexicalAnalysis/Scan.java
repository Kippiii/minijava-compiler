package LexicalAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ParserGenerator.*;

public class Scan {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 1)
            filename = "Factorial.java";
        else
            filename = args[0];

        int errors = 0;

        List<Token> tokens;
        try {
            tokens = scan(filename);
        } catch (ManyParseException e) {
            for (ParseException e2 : e.parseExceptions) {
                System.err.printf("%s:%s%n", filename, e2.toString());
            }

            errors = e.parseExceptions.size();
            tokens = e.tokens;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("lexer.out"));
        PrintWriter fileWriter = new PrintWriter(writer);
        for (Token token : tokens) {
            fileWriter.printf("%s:%03d%03d: %s \"%s\"%n", filename, token.beginLine, token.beginColumn, MiniJavaParserConstants.tokenImage[token.kind], token.image);
        }

        writer.close();

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static List<Token> scan(String file) throws ManyParseException, FileNotFoundException {
        final MiniJavaParser lexer = new MiniJavaParser(new FileInputStream(file));

        List<Token> tokens = new ArrayList<Token>();
        List<ParseException> errors = new ArrayList<ParseException>();
        while (true) {
            try {
                final Token token = lexer.getNextToken();
                tokens.add(token);
                if (token.kind == MiniJavaParserConstants.EOF)
                    break;
            } catch (TokenMgrError e) {
                errors.add(new ParseException(e.toString()));
            }
        }

        if (errors.size() > 0)
            throw new ManyParseException(errors, tokens);
        return tokens;
    }

}
