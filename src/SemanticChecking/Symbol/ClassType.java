package SemanticChecking.Symbol;

import java.util.*;

public class ClassType extends Type {
    private String name;
    private Symbol extName;
    private List<Symbol> varList;
    private HashMap<Symbol, Type> varTypes;
    public int lineNumber, colNumber;

    public ClassType(String name, int lineNumber, int colNumber) {
        this.name = name;
        this.extName = null;
        this.varList = new ArrayList<Symbol>();
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
        this.varList.add(s);
        this.varTypes.put(s, t);
    }

    public int getObjectSize(Map<Symbol, ClassType> symbolTable) {
        int upperSize = 0;
        if (this.extName != null) {
            upperSize = symbolTable.get(this.extName).getObjectSize(symbolTable);
        }
        return 4*(upperSize + this.varTypes.size());
    }

    public int getOffset(Symbol varName, Map<Symbol, ClassType> symbolTable) {
        int index = 0;
        for (; index < this.varList.size(); index++) {
            if (this.varList.get(index) == varName) {
                break;
            }
        }
        if (index == this.varList.size()) {
            // TODO Error
        }

        int upperSize = 0;
        if (this.extName != null) {
            upperSize = symbolTable.get(this.extName).getObjectSize(symbolTable);
        }

        return 4*(index + upperSize);
    }

    @Override
    public String toString() {
        return "class";
    }

}
