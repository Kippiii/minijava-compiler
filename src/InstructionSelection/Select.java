package InstructionSelection;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ErrorManagement.UnexpectedException;
import IRTranslation.Translate;
import IRTranslation.TranslateReturn;
import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import tree.NameOfTemp;
import tree.Stm;
import tree.TreePrint;
import assem.Instruction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select {
    /**
     * Main driving code for the instruction selection phase of the compiler
     * @param filename - The path to the source file
     * @param EASY_MAP - A simple temp map that maps all temps to %g3
     */
    static String filename = "Factorial.java";
    final static Map<NameOfTemp,String> EASY_MAP = new HashMap<NameOfTemp,String> () {
        @java.lang.Override
        public String get(Object t) {
            return "%g3";
        }
    };

    public static void main(String[] args) throws FileNotFoundException, UnexpectedException, IOException {
        /**
         * Code that is ran when the compiler is run starting at the instruction selection phase
         * @throws FileNotFoundException
         * @throws UnexpectedException
         * @throws IOException
         */
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            Map<Symbol, List<Instruction>> assembly = select(filename, debug);
            AssemblyWriter aw = new AssemblyWriter(filename.substring(0, filename.lastIndexOf('.')) + ".s");
            for (Map.Entry<Symbol, List<Instruction>> entry : assembly.entrySet()) {
                for (Instruction inst : entry.getValue()) {
                    aw.writeInstruction(inst,EASY_MAP);
                }
            }
            aw.close();
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static Map<Symbol, List<Instruction>> select(String filename) throws FileNotFoundException, CompilerExceptionList, UnexpectedException {
        /**
         * Generates the instructions (without register allocations) for a given source file
         * @param filename - The source file
         * @return A mapping from method names to lists of assembly instructions
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         * @throws UnexpectedException
         */
        return select(filename, false);
    }

    public static Map<Symbol, List<Instruction>> select(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList, UnexpectedException {
        /**
         * Generates the instructions (without register allocations) for a given source file
         * @param filename - The source file
         * @param debug - Whether to print debug output
         * @return A mapping from method names to lists of assembly instructions
         * @throws FileNotFoundException
         * @throws CompilerExceptionList
         * @throws UnexpectedException
         */
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

        return assembly;
    }

}
