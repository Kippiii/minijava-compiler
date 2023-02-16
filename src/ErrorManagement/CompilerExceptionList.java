package ErrorManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompilerExceptionList extends Exception implements Iterable<CompilerException> {

    private List<CompilerException> errors;

    public CompilerExceptionList(List<CompilerException> errs) {
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
