package LexicalAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseException extends Exception {
    static final String ERROR_RE = "Lexical error at line (\\d+), column (\\d+).  Encountered: '\\d+' \\(\\d+\\), after prefix \"(.)\"$";

    int lineNum;
    int colNum;
    char curChar;
    public ParseException(String errorMsg) {
        final Pattern r = Pattern.compile(ERROR_RE);
        final Matcher m = r.matcher(errorMsg);
        m.find();
        this.lineNum = Integer.parseInt(m.group(1));
        this.colNum = Integer.parseInt(m.group(2));
        this.curChar = m.group(3).charAt(0);
    }

    public String toString() {
        return String.format("%03d.%03d: ERROR -- illegal character %c", lineNum, colNum, curChar);
    }
}
