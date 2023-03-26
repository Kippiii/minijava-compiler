package Frame;

import java.util.List;
import tree.*;

public abstract class Frame {
    abstract public Frame newFrame(LABEL name, List<Boolean> formals);
    public LABEL name;
    public List<Access> formals;
    abstract public Access allocLocal(boolean escape);
}
