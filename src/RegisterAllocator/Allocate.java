package RegisterAllocator;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ErrorManagement.UnexpectedException;
import InstructionSelection.AssemblyWriter;
import SemanticChecking.Symbol.Symbol;
import assem.Instruction;
import tree.NameOfTemp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static InstructionSelection.Select.select;

public class Allocate {
    /**
     * Main driver code for the register allocation phase of the compiler
     * @param filename - The path to the source file
     * @param colors - A list of all registers to color to
     */

    static String filename = "Factorial.java";

    final static List<String> colors = Arrays.asList(
            "%l0", "%l1", "%l2", "%l3", "%l4", "%l5", "%l6", "%l7",
            "%g0", "%g1", "%g2", "%g3", "%g4", "%g5", "%g6", "%g7",
            "%i0", "%i1", "%i2", "%i3", "%i4", "%i5", "%i6", "%i7",
            "%o0", "%o1", "%o2", "%o3", "%o4", "%o5", "%o6", "%o7", "%fp"
    );  // TODO some of these should not be colored to

    public static void main(String[] args) throws UnexpectedException, IOException {
        /**
         * Ran when the compiler is ran from the register allocation phase
         * @throws UnexpectedException
         * @throws IOException
         */
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            Map<Symbol, List<Instruction>> assembly = select(filename);
            Map<Symbol, Map<NameOfTemp, String>> allColoring = allocate(assembly, debug);
            AssemblyWriter aw = new AssemblyWriter(filename.substring(0, filename.lastIndexOf('.')) + ".s");
            for (Map.Entry<Symbol, List<Instruction>> entry : assembly.entrySet()) {
                for (Instruction inst : entry.getValue()) {
                    aw.writeInstruction(inst, allColoring.get(entry.getKey()));
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

    public static Map<Symbol, Map<NameOfTemp, String>> allocate(Map<Symbol, List<Instruction>> assembly) {
        /**
         * Creates a mapping from temporaries to registers given some assembly code
         * @param assembly - Map from method names to their assembly code
         * @return A map from method names to maps that map temporaries to registers
         */
        return allocate(assembly, false);
    }

    public static Map<Symbol, Map<NameOfTemp, String>> allocate(Map<Symbol, List<Instruction>> assembly, boolean debug) {
        /**
         * Creates a mapping from temporaries to registers given some assembly code
         * @param assembly - Map from method names to their assembly code
         * @param debug - Whether to print debug output
         * @return A map from method names to maps that map temporaries to registers
         */
        Map<Symbol, Map<NameOfTemp, String>> allColorings = new HashMap<Symbol, Map<NameOfTemp, String>>();
        for (Map.Entry<Symbol, List<Instruction>> entry : assembly.entrySet()) {
            List<Instruction> methodAsm = entry.getValue();

            Set<NameOfTemp> temps = new HashSet<NameOfTemp>();
            for (Instruction inst : methodAsm) {
                if (inst.use() != null) {
                    for (NameOfTemp t : inst.use()) {
                        if (t != null) {
                            temps.add(t);
                        }
                    }
                }
                if (inst.def() != null) {
                    for (NameOfTemp t : inst.def()) {
                        if (t != null) {
                            temps.add(t);
                        }
                    }
                }
            }

            AssemFlowGraph flowGraph = new AssemFlowGraph(methodAsm, new ArrayList<NameOfTemp>(temps));
            if (debug) {
                System.out.println(flowGraph.toString());
            }

            AssemInterferenceGraph interferenceGraph = new AssemInterferenceGraph(flowGraph);
            Map<NameOfTemp, String> coloring = interferenceGraph.color(colors);
            if (debug) {
                System.out.println(interferenceGraph.toString());
                for (Map.Entry<NameOfTemp, String> colorEntry : coloring.entrySet()) {
                    System.out.println(colorEntry.getKey().toString() + " -> " + colorEntry.getValue());
                }
            }
            allColorings.put(entry.getKey(), coloring);
        }
        return allColorings;
    }

}
