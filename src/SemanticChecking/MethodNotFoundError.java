package SemanticChecking;

import ErrorManagement.CompilerException;

public class MethodNotFoundError extends CompilerException {
    String className, methodName;
    int lineNumber, colNumber;

    public MethodNotFoundError(String className, String methodName, int lineNumber, int colNumber) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return String.format("%03d.%03d: ERROR -- Undefined method '%s.%s'", this.lineNumber, this.colNumber, this.className, this.methodName);
    }

}
