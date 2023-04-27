package SemanticChecking.Symbol;

import java.util.*;

public class ClassType extends Type {
    /**
     * Represents the type of a class to be put in the symbol table
     * @param name - The string name of the class
     * @param extName - The name of its superclass (or null)
     * @param fields - A namespace containing all of the fields in the class
     * @param methods - A namespace containing all of the methods in the class
     * @param lineNumber - The line number in the source file that the class is defined at
     * @param colNumber - The column number in the source file that the class is defined at
     */
    private String name;
    private Symbol extName;
    private NameSpace fields;
    private NameSpace methods;
    public int lineNumber, colNumber;

    public ClassType(String name, int lineNumber, int colNumber) {
        this.name = name;
        this.extName = null;
        this.fields = new NameSpace();
        this.methods = new NameSpace();
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
    }

    public String getName() {
        /**
         * Gets the name of the class
         * @return The name of the class
         */
        return this.name;
    }
    public Symbol getExtName() {
        /**
         * Gets the name of the superclass
         * @return The name of the superclass as a symbol
         */
        return this.extName;
    }
    public Type getFieldType(Symbol s) {
        /**
         * Gets the type of a field by name
         * @param s - The symbol corresponding to the name of the symbol
         * @return The type of the field with that name, or null if it does not exist
         */
        return this.fields.getType(s);
    }
    public Enumeration<Symbol> getFieldSymbols() {
        /**
         * Gets an enumeration of all of the field symbols
         * @return An enumeration that contains a symbol for each field name
         */
        return this.fields.getEnum();
    }
    public MethodType getMethodType(Symbol s) {
        /**
         * Gets the type of a method by name
         * @param s - The symbol corresponding to the name of the method
         * @return The type of the method with that name, or null if it does not exist
         */
        return (MethodType) this.methods.getType(s);
    }
    public Enumeration<Symbol> getMethodSymbols() {
        /**
         * Gets an enumeration of all of the method symbols
         * @return An enumeration that contains a symbol for each method name
         */
        return this.methods.getEnum();
    }

    public void setExtName(Symbol s) {
        /**
         * Sets the name of the superclass
         * @param s - The symbol corresponding to the name of the superclass
         */
        this.extName = s;
    }
    public void setField(Symbol s, Type t) {
        /**
         * Adds a field to the namespace
         * @param s - A symbol corresponding to the name of the field being added
         * @param t - The type of the field
         */
        this.fields.add(s, t);
    }
    public void setMethod(Symbol s, MethodType t) {
        /**
         * Adds a method to the namespace
         * @param s - A symbol corresponding to the name of the method being added
         * @param t - The type of the method
         */
        this.methods.add(s, t);
    }

    public int getObjectSize(NameSpace symbolTable) {
        /**
         * Gets the size of an instance of this class when stored in memory
         * @param symbolTable - The symbol table corresponding to the source program
         * @return The size of an instance
         */
        int upperSize = 0;
        if (this.extName != null) {
            upperSize = ((ClassType) symbolTable.getType(this.extName)).getObjectSize(symbolTable);
        }
        return 4*(upperSize + this.fields.size());
    }

    public int getOffset(Symbol varName, NameSpace symbolTable) {
        /**
         * Gets the offset of a particular field in an instance of the class
         * @param varName - The symbol corresponding to the name of the field
         * @param symbolTable - The symbol table corresponding to the source program
         * @return The offset of the field
         */
        int index = this.fields.getNum(varName);

        int upperSize = 0;
        if (this.extName != null) {
            upperSize = ((ClassType) symbolTable.getType(this.extName)).getObjectSize(symbolTable);
        }

        return 4*index + upperSize;
    }

    @Override
    public String toString() {
        return "class";
    }

}
