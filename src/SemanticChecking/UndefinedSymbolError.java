package SemanticChecking;

import ErrorManagement.CompilerException;

public class UndefinedSymbolError extends CompilerException {
    String symbol;
    int lineNumber, colNumber;

    public UndefinedSymbolError(String symbol, int lineNumber, int colNumber) {
        super();
        this.symbol = symbol;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Undefined symbol '%s'", this.lineNumber, this.colNumber, this.symbol);
    }

}
