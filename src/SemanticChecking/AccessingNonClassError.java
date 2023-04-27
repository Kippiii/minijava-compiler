package SemanticChecking;

import ErrorManagement.CompilerException;

public class AccessingNonClassError extends CompilerException {
    /**
     * Represents a semantic error where access is attempted of a non-object
     * @param type - The name of the type that is being accessed
     * @param lineNumber - The line number of the access of the object
     * @param colNumber - The column number of the access of the object
     */
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
