package LexicalAnalysis;

import ParserGenerator.*;
import ErrorManagement.CompilerException;

public class LexException extends CompilerException {
    int lineNum;
    int colNum;
    char curChar;
    public LexException(Token token) {
        this.lineNum = token.beginLine;
        this.colNum = token.beginColumn;
        this.curChar = token.image.charAt(0);
    }

    public String toString() {
        String errMsg = String.format("%03d.%03d: ERROR -- illegal character ", lineNum, colNum);
        if (((int) curChar) <= 31 || ((int) curChar) == 127)
            return errMsg + Character.getName(curChar);
        return errMsg + Character.toString(curChar);
    }
}
