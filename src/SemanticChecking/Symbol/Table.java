package SemanticChecking.Symbol;

public abstract class Table {

    public abstract void put(Symbol s, Table t);
    public abstract Table get(Symbol s);

}
