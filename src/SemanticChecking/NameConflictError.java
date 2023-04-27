package SemanticChecking;

import ErrorManagement.CompilerException;

public class NameConflictError extends CompilerException {
    /**
     * Semantic error that is thrown when a name is defined that is already defined in this scope
     * @param name - The name being defined that already exists
     * @param lineNumber - The line number where the name is defined
     * @param colNumber - The column number where the name is defined
     */
    String name;
    int lineNumber, colNumber;

    public NameConflictError(String name, int lineNumber, int colNumber) {
        super();
        this.name = name;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Existing name already defined: %s", this.lineNumber, this.colNumber, this.name);
    }
}
