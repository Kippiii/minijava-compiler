package SemanticChecking.Symbol;

import java.util.*;

public class ClassType extends Type {
    private String name;
    private Symbol extName;
    private NameSpace fields;
    private NameSpace methods;
    public int lineNumber, colNumber;

    public ClassType(String name, int lineNumber, int colNumber) {
        this.name = name;
        this.extName = null;
        this.fields = new NameSpace();
        this.methods = new NameSpace();
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    public String getName() {
        return this.name;
    }
    public Symbol getExtName() {
        return this.extName;
    }
    public Type getFieldType(Symbol s) {
        return this.fields.getType(s);
    }
    public Enumeration<Symbol> getFieldSymbols() {
        return this.fields.getEnum();
    }
    public MethodType getMethodType(Symbol s) {
        return (MethodType) this.methods.getType(s);
    }
    public Enumeration<Symbol> getMethodSymbols() {
        return this.methods.getEnum();
    }

    public void setExtName(Symbol s) {
        this.extName = s;
    }
    public void setField(Symbol s, Type t) {
        this.fields.add(s, t);
    }
    public void setMethod(Symbol s, MethodType t) {
        this.methods.add(s, t);
    }

    public int getObjectSize(NameSpace symbolTable) {
        int upperSize = 0;
        if (this.extName != null) {
            upperSize = ((ClassType) symbolTable.getType(this.extName)).getObjectSize(symbolTable);
        }
        return 4*(upperSize + this.fields.size());
    }

    public int getOffset(Symbol varName, NameSpace symbolTable) {
        int index = this.fields.getNum(varName);

        int upperSize = 0;
        if (this.extName != null) {
            upperSize = ((ClassType) symbolTable.getType(this.extName)).getObjectSize(symbolTable);
        }

        return 4*(index + upperSize);
    }

    @Override
    public String toString() {
        return "class";
    }

}
