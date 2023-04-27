package SemanticChecking.Symbol;

import java.util.Dictionary;
import java.util.Hashtable;

public class Symbol {
    /**
     * This is an object to store strings to have quick comparisons
     * @param name - The corresponding string of the symbol
     * @param dict - The dictionary mapping strings to symbols
     */
    private String name;
    public Symbol(String n) {
        this.name = n;
    }
    private static Dictionary dict = new Hashtable();

    public String toString() {
        return this.name;
    }

    public static Symbol symbol(String n) {
        /**
         * Gets the symbol corresponding to a given string
         * @param n - The string to get the symbol of
         * @return The symbol corresponding to the string
         */
        String u = n.intern();
        Symbol s = (Symbol) dict.get(u);
        if (s == null) {
            s = new Symbol(u);
            dict.put(u, s);
        }
        return s;
    }

}
