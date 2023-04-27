package ErrorManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompilerExceptionList extends Exception implements Iterable<CompilerException> {
    /**
     * Represents a list of CompilerExceptions (many user errors)
     */

    private List<CompilerException> errors;

    public CompilerExceptionList(List<CompilerException> errs) {
        /**
         * Converts a list of errors into one throwable error
         * @param errs - The list of errors to be thrown
         */
        this.errors = new ArrayList<CompilerException>();
        for (CompilerException err : errs) {
            this.errors.add(err);
        }
    }

    @Override
    public Iterator<CompilerException> iterator() {
        return this.errors.iterator();
    }

}
