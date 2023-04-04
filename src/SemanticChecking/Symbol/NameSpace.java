package SemanticChecking.Symbol;

import java.util.*;

public class NameSpace {
    private Map<Symbol, Type> table;
    private List<Symbol> order;

    public NameSpace() {
        this.table = new HashMap<Symbol, Type>();
        this.order = new ArrayList<Symbol>();
    }

    public Type getType(Symbol s) {
        return this.table.get(s);
    }
    public Symbol getSymbol(int i) {
        return this.order.get(i);
    }
    public Type getType(int i) {
        return this.table.get(this.getSymbol(i));
    }
    public int getNum(Symbol s) {
        return this.order.indexOf(s);
    }
    public Enumeration<Symbol> getEnum() {
        return Collections.enumeration(this.table.keySet());
    }
    public int size() {
        return this.order.size();
    }

    public void add(Symbol s, Type t) {
        Type curVal = this.table.get(s);
        if (curVal == null) {
            this.table.put(s, t);
            this.order.add(s);
        }
    }

}
