package SemanticChecking;

import ErrorManagement.CompilerException;

public class MethodNotFoundError extends CompilerException {
    /**
     * Semantic error that is thrown when a method is called that is not defined
     * @param className - The name of the class being called from
     * @param methodName - The name of the method that is called that does not exist
     * @param lineNumber - The line number of the method call
     * @param colNumber - The column number of the method call
     */
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
