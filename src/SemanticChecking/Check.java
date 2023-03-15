package SemanticChecking;

import AbstractSyntax.BuildAbstract;
import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import SemanticChecking.Symbol.ClassType;
import SemanticChecking.Symbol.Symbol;
import syntax.Program;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Check {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
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

    public static Program check(String filename) throws FileNotFoundException, CompilerExceptionList {
        return check(filename, false);
    }

    public static Program check(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        Program program = BuildAbstract.buildAbstractTree(filename);

        // Create symbol table
        HashMap<Symbol, ClassType> symbolTable;
        SymbolTableFactory stf;
        if (debug)
            stf = new SymbolTableFactory(true);
        else
            stf = new SymbolTableFactory();
        stf.visit(program);
        if (stf.errors.size() > 0)
            throw new CompilerExceptionList(stf.errors);
        symbolTable = stf.classes;

        // TODO Print symbol table

        // TODO Check typing and variable existence

        return program;
    }

}