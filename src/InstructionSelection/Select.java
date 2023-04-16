package InstructionSelection;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ErrorManagement.UnexpectedException;
import IRTranslation.Translate;
import IRTranslation.TranslateReturn;
import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import tree.Stm;
import tree.TreePrint;
import assem.Instruction;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException, UnexpectedException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            select(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static void select(String filename) throws FileNotFoundException, CompilerExceptionList, UnexpectedException {
        select(filename, false);
    }

    public static void select(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList, UnexpectedException {
        TranslateReturn r = Translate.translate(filename);
        Map<Symbol, Stm> fragments = r.fragments();
        NameSpace symbolTable = r.symbolTable();
        Map<Symbol, List<Stm>> flattenedFragments = new HashMap<Symbol, List<Stm>>();
        for (Map.Entry<Symbol, Stm> entry : fragments.entrySet()) {
            flattenedFragments.put(entry.getKey(), canon.Main.transform(entry.getValue()));
        }

        if (debug) {
            for (Map.Entry<Symbol, List<Stm>> entry : flattenedFragments.entrySet()) {
                System.out.printf("!  Procedure fragment %s%n", entry.getKey().toString());
                for (Stm s : entry.getValue()) {
                    System.out.print(TreePrint.toString(s));
                }
                System.out.println("!  End fragment");
                System.out.println();
            }
        }

        Map<Symbol, List<Instruction>> assembly = new HashMap<Symbol, List<Instruction>>();
        Codegen c = new Codegen();
        for (Map.Entry<Symbol, List<Stm>> entry : flattenedFragments.entrySet()) {
            assembly.put(entry.getKey(), c.codegen(entry.getKey(), entry.getValue(), symbolTable));
        }

        if (debug) {
            for (Map.Entry<Symbol, List<Instruction>> entry : assembly.entrySet()) {
                System.out.printf("!  Procedure fragment %s%n", entry.getKey().toString());
                for (Instruction i : entry.getValue()) {
                    System.out.println(i.format());
                }
                System.out.println("!  End fragment");
                System.out.println();
            }
        }
    }

}
