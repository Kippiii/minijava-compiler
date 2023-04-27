package InstructionSelection;

import ErrorManagement.UnexpectedException;
import tree.Exp;
import tree.Stm;
import tree.TreePrint;

public class IRParseException extends UnexpectedException {
    /**
     * Represents an error where invalid IR code is given to the module
     */

    public IRParseException(Stm s) {
        super(TreePrint.toString(s));
    }

    public IRParseException(Exp e) {
        super(TreePrint.toString(e));
    }

}
