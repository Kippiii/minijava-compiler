package SemanticChecking;

import ErrorManagement.CompilerException;

public class InvalidClassError extends CompilerException {
    String className;
    int lineNumber, colNumber;

    public InvalidClassError(String className, int lineNumber, int colNumber) {
        super();
        this.className = className;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Undefined class name '%s'", this.lineNumber, this.colNumber, this.className);
    }

}
