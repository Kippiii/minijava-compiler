package SemanticChecking;

import ErrorManagement.CompilerException;

public class AccessingNonClassError extends CompilerException {
    String type;
    int lineNumber, colNumber;

    public AccessingNonClassError(String type, int lineNumber, int colNumber) {
        super();
        this.type = type;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Accessing non-class type '%s'", this.lineNumber, this.colNumber, this.type);
    }

}
