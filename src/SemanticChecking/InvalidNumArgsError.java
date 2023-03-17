package SemanticChecking;

import ErrorManagement.CompilerException;

public class InvalidNumArgsError extends CompilerException {
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
