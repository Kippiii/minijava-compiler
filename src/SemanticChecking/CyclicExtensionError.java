package SemanticChecking;

import ErrorManagement.CompilerException;

public class CyclicExtensionError extends CompilerException {
    /**
     * Represents an error where a class is a subclass of itself
     * @param className - The name of the class that is a subclass of itself
     * @param lineNumber - The line number where the class was declared
     * @param colNumber - The column number where the class was declared
     */
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
