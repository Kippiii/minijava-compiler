package SemanticChecking;

import ErrorManagement.CompilerException;

public class TypeMismatchError extends CompilerException {
    String expectedType, gottenType;
    int lineNumber, colNumber;

    public TypeMismatchError(String expectedType, String gottenType, int lineNumber, int colNumber) {
        super();
        this.expectedType = expectedType;
        this.gottenType = gottenType;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Expected type '%s', got type '%s'", this.lineNumber, this.colNumber, this.expectedType, this.gottenType);
    }

}
