package LexicalAnalysis;

import ParserGenerator.*;

public class ParseException extends Exception {
    int lineNum;
    int colNum;
    char curChar;
    public ParseException(Token token) {
        this.lineNum = token.beginLine;
        this.colNum = token.beginColumn;
        this.curChar = token.image.charAt(0);
    }

    public String toString() {
        return String.format("%03d.%03d: ERROR -- illegal character %c", lineNum, colNum, curChar);
    }
}
