package LexicalAnalysis;

import java.util.ArrayList;
import java.util.List;

import ParserGenerator.*;

public class ManyLexException extends Exception {
    List<LexException> lexExceptions;
    public ManyLexException(List<LexException> lexExceptions) {
        this.lexExceptions = new ArrayList<LexException>();
        for (LexException pe : lexExceptions)
            this.lexExceptions.add(pe);
    }
}
