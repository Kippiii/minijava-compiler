package InstructionSelection;

import ErrorManagement.UnexpectedException;
import tree.Exp;
import tree.Stm;
import tree.TreePrint;

public class IRParseException extends UnexpectedException {

    public IRParseException(Stm s) {
        super(TreePrint.toString(s));
    }

    public IRParseException(Exp e) {
        super(TreePrint.toString(e));
    }

}
