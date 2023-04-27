package SemanticChecking.Symbol;

public class BasicType extends Type {
    /**
     * Represents a type that is only defined by its name
     * @param name - The string name of the type
     */
    String name;

    public BasicType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
