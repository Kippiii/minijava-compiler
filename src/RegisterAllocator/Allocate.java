package RegisterAllocator;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ErrorManagement.UnexpectedException;
import InstructionSelection.AssemblyWriter;
import SemanticChecking.Symbol.Symbol;
import assem.Instruction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static InstructionSelection.Select.select;

public class Allocate {

    static String filename = "Factorial.java";

    public static void main(String[] args) throws UnexpectedException, IOException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            Map<Symbol, List<Instruction>> assembly = select(filename);
            Map<Symbol, List<Instruction>> newAssembly = allocate(assembly, debug);
            AssemblyWriter aw = new AssemblyWriter(filename.substring(0, filename.lastIndexOf('.')) + ".s");
            for (Map.Entry<Symbol, List<Instruction>> entry : assembly.entrySet()) {
                for (Instruction inst : entry.getValue()) {
                    aw.writeInstruction(inst);
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

    public static Map<Symbol, List<Instruction>> allocate(Map<Symbol, List<Instruction>> assembly) {
        return allocate(assembly, false);
    }

    public static Map<Symbol, List<Instruction>> allocate(Map<Symbol, List<Instruction>> assembly, boolean debug) {
        AssemFlowGraph flowGraph = new AssemFlowGraph(assembly, ?); // TODO Get temp list?
        return null; // TODO
    }

}
