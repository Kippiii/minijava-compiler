package SemanticChecking.Symbol;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class NameSpace {
    private Map<Symbol, Type> table;
    private List<Symbol> order;

    public NameSpace() {
        this.table = null;
        this.order = null;
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
