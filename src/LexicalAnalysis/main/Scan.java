package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.*;

public class Scan {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 1)
            filename = "Factorial.java";
        else
            filename = args[0];

        final MiniJavaParser lexer = new MiniJavaParser (new FileInputStream(filename));

        int errors = 0;
        while (true) {
            try {
                final Token token = lexer.getNextToken();
                if (token.kind == MiniJavaParserConstants.EOF)
                    break;
            } catch (TokenMgrError e) {
                System.err.println(parseError(e.getMessage()));
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    static String ERROR_RE = "^Lexical error at line (\\d+), column (\\d+).  Encountered: '\\d+' \\(\\d+\\), after prefix \"(.)\"$";
    static String parseError(String errorMsg) {
        Pattern r = Pattern.compile(ERROR_RE);
        Matcher m = r.matcher(errorMsg);
        m.find();
        int lineNum = Integer.parseInt(m.group(1));
        int colNum = Integer.parseInt(m.group(2));
        char curChar = m.group(3).charAt(0);
        return String.format("%s:%03d.%03d: ERROR -- illegal character %c", filename, lineNum, colNum, curChar);
    }

}
