package SemanticChecking.Symbol;

public class BasicType extends Type {
    String name;

    public BasicType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
