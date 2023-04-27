package SemanticChecking;

import ErrorManagement.CompilerException;

public class UndefinedSymbolError extends CompilerException {
    /**
     * Semantic error thrown when a symbol is used that is not defined
     * @param symbol - The symbol being used
     * @param lineNumber - The line number where the symbol is used
     * @param colNumber - The column number where the symbol is used
     */
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
