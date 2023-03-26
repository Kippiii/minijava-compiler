package SemanticChecking;

import ErrorManagement.CompilerException;

public class CyclicExtensionError extends CompilerException {
    String className;
    int lineNumber, colNumber;

    public CyclicExtensionError(String className, int lineNumber, int colNumber) {
        this.className = className;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Class '%s' inherits itself", this.lineNumber, this.colNumber, this.className);
    }

}
