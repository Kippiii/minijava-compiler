package Parsing;

import ErrorManagement.CompilerException;
import ParserGenerator.ParseException;

public class MyParseException extends CompilerException {
    /**
     * Represents a parsing error, where a rule expects one symbol but gets another
     * @param lineNum - The line number of the unexpected token
     * @param colNum - The column number of the unexpected token
     * @param token - The value of the unexpected token
     */

    int lineNum;
    int colNum;
    String token;

    public MyParseException(ParseException exc) {
        /**
         * Creates an instance of a ParseException
         * @param exc - The parsing exception thrown by JavaCC
         */
        this.lineNum = exc.currentToken.next.beginLine;
        this.colNum = exc.currentToken.next.beginColumn;
        this.token = exc.currentToken.next.image;
    }

    public String toString() {
        /**
         * Converts the parsing exception to a string for the user
         * @return The string version of the error shown to the user
         */
        return String.format("%03d.%03d: ERROR - Unexpected token '%s'", this.lineNum, this.colNum, this.token);
    }

}
