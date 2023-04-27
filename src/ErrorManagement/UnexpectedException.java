package ErrorManagement;

public abstract class UnexpectedException extends Exception {
    /**
     * Represents an exception thrown due to compiler error
     */

    public UnexpectedException(String s) {
        super(s);
    }

}
