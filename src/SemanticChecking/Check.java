package SemanticChecking;

import AbstractSyntax.BuildAbstract;
import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import SemanticChecking.Symbol.*;
import syntax.Program;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Check {
    /**
     * The driver code for the semantic checking phase of the compiler
     * @param filename - The source file path for the compiler
     */
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * Ran by the main program when it is started from the semantic checking phase
         * @throws FileNotFoundException
         */
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            check(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static CheckReturn check(String filename) throws FileNotFoundException, CompilerExceptionList {
        /**
         * Runs semantic checking of the compiler
         * @param filename - The path to the source file
         * @return The abstract syntax tree and the symbol table
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         */
        return check(filename, false);
    }

    public static CheckReturn check(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        /**
         * Runs semantic checking of the compiler
         * @param filename - The path to the source file
         * @param debug - Whether to print the debug output
         * @return The abstract syntax tree and the symbol table
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         */
        Program program = BuildAbstract.buildAbstractTree(filename);

        // Create symbol table
        NameSpace symbolTable;
        SymbolTableFactory stf;
        if (debug)
            stf = new SymbolTableFactory(true);
        else
            stf = new SymbolTableFactory();
        stf.visit(program);
        if (stf.errors.size() > 0)
            throw new CompilerExceptionList(stf.errors);
        symbolTable = stf.classes;

        // Print symbol table
        if (debug) {
            for (Symbol className : Collections.list(symbolTable.getEnum())) {
                ClassType ct = (ClassType) symbolTable.getType(className);
                System.out.println("Class " + className.toString() + " extends " + ct.getExtName() + ":");
                for (Symbol fieldName : Collections.list(ct.getFieldSymbols())) {
                    System.out.print("\t" + className.toString() + "." + fieldName.toString() + "[");
                    System.out.print(ct.getFieldType(fieldName).toString());
                }
                for (Symbol methodName : Collections.list(ct.getMethodSymbols())) {
                    MethodType type = (MethodType) ct.getMethodType(methodName);
                    System.out.print("ret type: " + type.getRetType().toString() + "; ");
                    System.out.print("formals: {");
                    for (Symbol formalSym : Collections.list(type.getArgSymbols())) {
                        System.out.print(formalSym.toString() + "=" + type.getArgType(formalSym).toString() + ",");
                    }
                    System.out.print("}; locals: {");
                    for (Symbol localSym : Collections.list(type.getLocalSymbols())) {
                        System.out.print(localSym.toString() + "=" + type.getLocalType(localSym).toString() + ",");
                    }
                    System.out.print("}");
                }
                System.out.println("]");
            }
        }

        // Check for cyclic inheritance issues
        checkInheritance(symbolTable);

        // TODO Record case?

        // Check typing and variable existence
        TypeChecker tc = new TypeChecker(symbolTable);
        tc.visit(program);
        if (tc.errors.size() > 0)
            throw new CompilerExceptionList(tc.errors);

        return new CheckReturn(program, symbolTable);
    }

    static void checkInheritance(NameSpace symbolTable) throws CompilerExceptionList {
        /**
         * Checks if there is a cyclic inheritance in the program
         * @param symbolTable - The symbol table of the program
         * @throws CompilerExceptionList
         */
        for (Symbol s : Collections.list(symbolTable.getEnum())) {
            ClassType ct = findCycle(symbolTable, s, new HashSet<Symbol>());
            if (ct != null) {
                List<CompilerException> errors = new ArrayList<CompilerException>();
                errors.add(new CyclicExtensionError(ct.getName(), ct.lineNumber, ct.colNumber));
                throw new CompilerExceptionList(errors);
            }
        }
    }

    static ClassType findCycle(NameSpace symbolTable, Symbol cur, Set<Symbol> visited) {
        /**
         * Checks for an inheritance cycle that starts at a particular class
         * @param symbolTable - The symbol table of the program
         * @param cur - A symbol corresponding to the name of the class that we are starting from
         * @param visited - The set of classes that have been visited in the dfs so far
         */
        if (cur == null) {
            return null;
        }
        if (visited.contains(cur)) {
            return (ClassType) symbolTable.getType(cur);
        }
        visited.add(cur);
        Symbol next = ((ClassType) symbolTable.getType(cur)).getExtName();
        return findCycle(symbolTable, next, visited);
    }

}