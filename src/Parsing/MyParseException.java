package Parsing;

import ErrorManagement.CompilerException;
import ParserGenerator.ParseException;

public class MyParseException extends CompilerException {

    int lineNum;
    int colNum;
    String token;

    public MyParseException(ParseException exc) {
        this.lineNum = exc.currentToken.next.beginLine;
        this.colNum = exc.currentToken.next.beginColumn;
        this.token = exc.currentToken.next.image;
    }

    public String toString() {
        return String.format("%03d.%03d: ERROR - Unexpected token '%s'", this.lineNum, this.colNum, this.token);
    }

}
