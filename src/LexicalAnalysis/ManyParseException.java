package LexicalAnalysis;

import java.util.ArrayList;
import java.util.List;

import ParserGenerator.*;

public class ManyParseException extends Exception {
    List<ParseException> parseExceptions;
    List<Token> tokens;
    public ManyParseException(List<ParseException> parseExceptions, List<Token> tokens) {
        this.parseExceptions = new ArrayList<ParseException>();
        for (ParseException pe : parseExceptions)
            this.parseExceptions.add(pe);
        this.tokens = new ArrayList<Token>();
        for (Token token : tokens)
            this.tokens.add(token);
    }
}
