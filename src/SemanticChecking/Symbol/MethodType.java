package SemanticChecking.Symbol;

import java.util.*;

public class MethodType extends Type {
    private String name;
    private Type retType;
    private NameSpace args;
    private NameSpace locals;

    public MethodType(String name) {
        this.name = name;
        this.retType = null;
        this.args = new NameSpace();
        this.locals = new NameSpace();
    }

    public String getName() {
        return this.name;
    }
    public Type getRetType() {
        return this.retType;
    }
    public Type getArgType(Symbol s) {
        return this.args.getType(s);
    }
    public Type getArgType(int i) {
        return this.args.getType(i);
    }
    public int getNumArgs() {
        return this.args.size();
    }
    public Enumeration<Symbol> getArgSymbols() {
        return this.args.getEnum();
    }
    public Type getLocalType(Symbol s) {
        return this.locals.getType(s);
    }
    public Enumeration<Symbol> getLocalSymbols() {
        return this.locals.getEnum();
    }
    public Type getType(Symbol s) {
        Type t = this.getArgType(s);
        if (t == null)
            return this.getLocalType(s);
        return t;
    }

    public int getArgNum(Symbol s) {
        return this.args.getNum(s);
    }
    public int getLocalNum(Symbol s) {
        return this.locals.getNum(s);
    }

    public void setRetType(Type r) {
        this.retType = r;
    }
    public void setArg(Symbol s, Type t) {
        this.args.add(s, t);
    }
    public void setLocal(Symbol s, Type t) {
        this.locals.add(s, t);
    }

    @Override
    public String toString() {
        return "method";
    }

}
