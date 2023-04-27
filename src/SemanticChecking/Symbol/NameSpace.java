package SemanticChecking.Symbol;

import java.util.*;

public class NameSpace {
    /**
     * Represents a mapping between symbols and types
     * @param table - A hashmap between symbols and types
     * @param order - The order that the symbols were added
     */
    private Map<Symbol, Type> table;
    private List<Symbol> order;

    public NameSpace() {
        this.table = new HashMap<Symbol, Type>();
        this.order = new ArrayList<Symbol>();
    }

    public Type getType(Symbol s) {
        /**
         * Gets the type corresponding to a symbol
         * @param s - The symbol
         * @return The corresponding type
         */
        return this.table.get(s);
    }
    public Symbol getSymbol(int i) {
        /**
         * Gets the symbol corresponding to a number
         * @param i
         * @return The symbol of the i-th value added
         */
        return this.order.get(i);
    }
    public Type getType(int i) {
        /**
         * Gets the type corresponding to a number
         * @param i
         * @return The type of the i-th value added
         */
        return this.table.get(this.getSymbol(i));
    }
    public int getNum(Symbol s) {
        /**
         * Gets the number corresponding to a symbol
         * @param s - The symbol to get the number of
         * @return The number corresponding to the symbol
         */
        return this.order.indexOf(s);
    }
    public Enumeration<Symbol> getEnum() {
        /**
         * Gets an enumeration of the symbols added
         * @return The enumeration of the symbols in the namespace
         */
        return Collections.enumeration(this.table.keySet());
    }
    public int size() {
        /**
         * Gets the number of items in the namespace
         * @return The number of items in the namespace
         */
        return this.order.size();
    }

    public void add(Symbol s, Type t) {
        /**
         * Adds an element to the symbol table
         * @param s - The symbol of the entry
         * @param t - The type of the entry
         */
        Type curVal = this.table.get(s);
        if (curVal == null) {
            this.table.put(s, t);
            this.order.add(s);
        }
    }

}
