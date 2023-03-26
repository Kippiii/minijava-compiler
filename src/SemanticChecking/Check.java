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

        // Print symbol table
        if (debug) {
            for (Map.Entry<Symbol, ClassType> classEntry : symbolTable.entrySet()) {
                System.out.println("Class " + classEntry.getKey().toString() + " extends " + classEntry.getValue().getExtName() + ":");
                for (Symbol classVar : Collections.list(classEntry.getValue().getVarSymbols())) {
                    System.out.print("\t" + classEntry.getKey().toString() + "." + classVar.toString() + "[");
                    if (classEntry.getValue().getVarType(classVar) instanceof BasicType) {
                        System.out.print(classEntry.getValue().getVarType(classVar).toString());
                    } else if (classEntry.getValue().getVarType(classVar) instanceof MethodType) {
                        MethodType type = (MethodType) classEntry.getValue().getVarType(classVar);
                        System.out.print("ret type: " + type.getRetType().toString() + "; ");
                        System.out.print("formals: {");
                        for (Symbol formalSym : Collections.list(type.getArgSymbols())) {
                            System.out.print(formalSym.toString() + "=" + type.getArgType(formalSym).toString() + ",");
                        }
                        System.out.print("}; locals: {");
                        for (Symbol localSym : Collections.list(type.getVarSymbols())){
                            System.out.print(localSym.toString() + "=" + type.getVarType(localSym).toString() + ",");
                        }
                        System.out.print("}");
                    }
                    System.out.println("]");
                }
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

        return program;
    }

    static void checkInheritance(Map<Symbol, ClassType> symbolTable) throws CompilerExceptionList {
        for (Symbol s : symbolTable.keySet()) {
            ClassType ct = findCycle(symbolTable, s, new HashSet<Symbol>());
            if (ct != null) {
                List<CompilerException> errors = new ArrayList<CompilerException>();
                errors.add(new CyclicExtensionError(ct.getName(), ct.lineNumber, ct.colNumber));
                throw new CompilerExceptionList(errors);
            }
        }
    }

    static ClassType findCycle(Map<Symbol, ClassType> symbolTable, Symbol cur, Set<Symbol> visited) {
        if (cur == null) {
            return null;
        }
        if (visited.contains(cur)) {
            return symbolTable.get(cur);
        }
        visited.add(cur);
        Symbol next = symbolTable.get(cur).getExtName();
        return findCycle(symbolTable, next, visited);
    }

}