package LexicalAnalysis;

import ParserGenerator.*;
import ErrorManagement.CompilerException;

public class LexException extends CompilerException {
    /**
     * Represent a lexical error in the compiler (when a character is unexpected in the source file)
     * @param lineNum - The line number of the unexpected character
     * @param colNum - The column number of the unexpected character
     * @param curChar - The character that was not expected
     */
    int lineNum;
    int colNum;
    char curChar;
    public LexException(Token token) {
        /**
         * Creates a LexException instance
         * @param token - The JavaCC Token object that was not expected
         */
        this.lineNum = token.beginLine;
        this.colNum = token.beginColumn;
        this.curChar = token.image.charAt(0);
    }

    public String toString() {
        /**
         * Converts this exception to a string to be seen by the user
         * @return The string output to the user
         */
        String errMsg = String.format("%03d.%03d: ERROR -- illegal character ", lineNum, colNum);
        if (((int) curChar) <= 31 || ((int) curChar) == 127)
            return errMsg + Character.getName(curChar);
        return errMsg + Character.toString(curChar);
    }
}
