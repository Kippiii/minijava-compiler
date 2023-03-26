package SemanticChecking.Symbol;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

public class ClassType extends Type {
    private String name;
    private Symbol extName;
    private HashMap<Symbol, Type> varTypes;
    public int lineNumber, colNumber;

    public ClassType(String name, int lineNumber, int colNumber) {
        this.name = name;
        this.extName = null;
        this.varTypes = new HashMap<Symbol, Type>();
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    public String getName() {
        return this.name;
    }
    public Symbol getExtName() {
        return this.extName;
    }
    public Type getVarType(Symbol s) {
        return this.varTypes.get(s);
    }
    public Enumeration<Symbol> getVarSymbols() {
        return Collections.enumeration(this.varTypes.keySet());
    }

    public void setExtName(Symbol s) {
        this.extName = s;
    }
    public void setVar(Symbol s, Type t) {
        this.varTypes.put(s, t);
    }

    @Override
    public String toString() {
        return "class";
    }

}
