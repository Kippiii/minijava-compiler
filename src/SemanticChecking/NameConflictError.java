package SemanticChecking;

import ErrorManagement.CompilerException;

public class NameConflictError extends CompilerException {
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
