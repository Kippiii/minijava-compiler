package SemanticChecking;

import ErrorManagement.CompilerException;

public class InvalidNumArgsError extends CompilerException {
    /**
     * Semantic error that occurs when a method is called with an invalid number of arguments
     * @param expNum - The expected number of arguments
     * @param gotNum - The number of arguments used in the call
     * @param lineNumber - The line number where the method is called
     * @param colNumber - The column number where the method is called
     */
    int expNum, gotNum, lineNumber, colNumber;

    public InvalidNumArgsError(int expNum, int gotNum, int lineNumber, int colNumber) {
        super();
        this.expNum = expNum;
        this.gotNum = gotNum;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Expected %d params, got %d", this.lineNumber, this.colNumber, this.expNum, this.gotNum);
    }

}
