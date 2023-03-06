package SemanticChecking.Symbol;

import java.util.Dictionary;
import java.util.Hashtable;

public class Symbol {
    private String name;
    private Symbol(String n) {
        this.name = n;
    }
    private static Dictionary dict = new Hashtable();

    public String toString() {
        return this.name;
    }

    public static Symbol symbol(String n) {
        String u = n.intern();
        Symbol s = (Symbol) dict.get(u);
        if (s == null) {
            s = new Symbol(u);
            dict.put(u, s);
        }
        return s;
    }

}
