package SemanticChecking;

import ErrorManagement.CompilerException;

public class InvalidClassError extends CompilerException {
    /**
     * Represents a semantic error where a class is accessed that is not defined
     * @param className - The name of the class that is accessed that is not defined
     * @param lineNumber - The line where the class is accessed
     * @param colNumber - The column where the class is accessed
     */
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
