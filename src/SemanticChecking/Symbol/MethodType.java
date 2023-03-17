package SemanticChecking.Symbol;

import java.util.*;

public class MethodType extends Type {
    private String name;
    private Type retType;
    private HashMap<Symbol, Type> argTypes;
    private List<Type> orderedArgs;
    private HashMap<Symbol, Type> varTypes;

    public MethodType(String name) {
        this.name = name;
        this.retType = null;
        this.argTypes = new HashMap<Symbol, Type>();
        this.orderedArgs = new ArrayList<Type>();
        this.varTypes = new HashMap<Symbol, Type>();
    }

    public String getName() {
        return this.name;
    }
    public Type getRetType() {
        return this.retType;
    }
    public Type getArgType(Symbol s) {
        return this.argTypes.get(s);
    }
    public Type getArgType(int i) {
        return this.orderedArgs.get(i);
    }
    public int getNumArgs() {
        return this.orderedArgs.size();
    }
    public Enumeration<Symbol> getArgSymbols() {
        return Collections.enumeration(this.argTypes.keySet());
    }
    public Type getVarType(Symbol s) {
        return this.varTypes.get(s);
    }
    public Enumeration<Symbol> getVarSymbols() {
        return Collections.enumeration(this.varTypes.keySet());
    }
    public Type getType(Symbol s) {
        Type t = this.getArgType(s);
        if (t == null)
            return this.getVarType(s);
        return t;
    }

    public void setRetType(Type r) {
        this.retType = r;
    }
    public void setArg(Symbol s, Type t) {
        this.argTypes.put(s, t);
        this.orderedArgs.add(t);
    }
    public void setVar(Symbol s, Type t) {
        this.varTypes.put(s, t);
    }

    @Override
    public String toString() {
        return "method";
    }

}
