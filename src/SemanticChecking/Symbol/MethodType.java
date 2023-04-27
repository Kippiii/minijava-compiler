package SemanticChecking.Symbol;

import java.util.*;

public class MethodType extends Type {
    /**
     * Stores information about the type of a method
     * @param name - The name of the method
     * @param retType - The type of the return value
     * @param args - A namespace for the formal parameters of the method
     * @param locals - The namespace for the local parameters of the method
     * @param isMain - Whether the method is the main method
     */
    private String name;
    private Type retType;
    private NameSpace args;
    private NameSpace locals;
    private boolean isMain;

    public MethodType(String name) {
        this.name = name;
        this.retType = null;
        this.args = new NameSpace();
        this.locals = new NameSpace();
        this.isMain = false;
    }

    public String getName() {
        /**
         * Gets the name of the method
         * @return The string name of the method
         */
        return this.name;
    }
    public Type getRetType() {
        /**
         * Gets the return type of the method
         * @return The return type of the method
         */
        return this.retType;
    }
    public Type getArgType(Symbol s) {
        /**
         * Gets the type of a formal parameter by its name
         * @param s - The symbol corresponding to the name of the formal parameter
         * @return The type corresponding to that formal parameter
         */
        return this.args.getType(s);
    }
    public Type getArgType(int i) {
        /**
         * Gets the type of a formal parameter by its number
         * @param i - The number of formal parameter
         * @return The type corresponding to that formal parameter
         */
        return this.args.getType(i);
    }
    public int getNumArgs() {
        /**
         * Gets the number of formal parameters of the method
         * @return The number of formal parameters of the method
         */
        return this.args.size();
    }
    public int getNumLocals() {
        /**
         * Gets the number of local variables in the method
         * @return The number of local variables in the method
         */
        return this.locals.size();
    }
    public Enumeration<Symbol> getArgSymbols() {
        /**
         * Gets an enumeration of the names of the formal parameters
         * @return An enumeration of formal parameters
         */
        return this.args.getEnum();
    }
    public Type getLocalType(Symbol s) {
        /**
         * Gets the type of a local variable by name
         * @param s - The symbol corresponding to the name of the local variable
         * @return The type of the local variable
         */
        return this.locals.getType(s);
    }
    public Enumeration<Symbol> getLocalSymbols() {
        /**
         * Gets an enumeration of names of local variables
         * @return An enumeration of symbols corresponding to local variable names
         */
        return this.locals.getEnum();
    }
    public Type getType(Symbol s) {
        /**
         * Returns the type of a variable (either formal parameter or local)
         * @param s - A symbol corresponding to the name of the variable
         * @return The type of the variable
         */
        Type t = this.getArgType(s);
        if (t == null)
            return this.getLocalType(s);
        return t;
    }

    public int getArgNum(Symbol s) {
        /**
         * Returns the number of a formal parameter
         * @param s - The symbol corresponding to the name of the formal parameter
         * @return The number of the formal parameter
         */
        return this.args.getNum(s);
    }
    public int getLocalNum(Symbol s) {
        /**
         * Returns the number of a local variable
         * @param s - The symbol corresponding to the name of the local variable
         * @return The number of the local variable
         */
        return this.locals.getNum(s);
    }

    public void setRetType(Type r) {
        /**
         * Sets the return type of the method
         * @param r - The return type of the method
         */
        this.retType = r;
    }
    public void setArg(Symbol s, Type t) {
        /**
         * Adds a formal parameter to the method
         * @param s - A symbol corresponding to the name of the formal parameter
         * @param t - The type of the formal parameter
         */
        this.args.add(s, t);
    }
    public void setLocal(Symbol s, Type t) {
        /**
         * Adds a local variable to the method
         * @param s - A symbol corresponding to the name of the local variable
         * @param t - The type of the local variable
         */
        this.locals.add(s, t);
    }

    public void setMain() {
        /**
         * Sets the method as the main method
         */
        this.isMain = true;
    }
    public boolean getMain() {
        /**
         * Gets whether the method is the main method
         * @return Whether the method is the main method
         */
        return this.isMain;
    }

    @Override
    public String toString() {
        return "method";
    }

}
