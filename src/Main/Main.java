package Main;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import ErrorManagement.UnexpectedException;
import InstructionSelection.AssemblyWriter;
import InstructionSelection.Select;
import RegisterAllocator.Allocate;
import SemanticChecking.Symbol.Symbol;
import assem.Instruction;
import tree.NameOfTemp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws UnexpectedException, IOException {
        if (args.length < 1) {
            System.err.println("File name not provided");
            return;
        }
        String filename = args[0];

        int errors = 0;
        try {
            Map<Symbol, List<Instruction>> assembly = Select.select(filename);
            Map<Symbol, Map<NameOfTemp, String>> allColoring = Allocate.allocate(assembly);
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

}
