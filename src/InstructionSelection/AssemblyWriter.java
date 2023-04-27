package InstructionSelection;

import assem.Instruction;
import tree.NameOfTemp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AssemblyWriter {
    /**
     * Writes assembly code to a file
     * @param pw - The PrintWriter that the assembly code is written to
     */
    private PrintWriter pw;

    public AssemblyWriter(String filename) throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename));
    }

    public void writeInstruction(Instruction inst) {
        /**
         * Writes an assembly instruction to the file
         * @param inst - The instruction being written
         */
        this.writeInstruction(inst, null);
    }

    public void writeInstruction(Instruction inst, Map<NameOfTemp, String> map) {
        /**
         * Writes an assembly instruction to the file
         * @param inst - The instruction being written
         * @param map - Mapping from names of temporaries to strings to put in their place
         */
        if (map == null) {
            this.pw.println(inst.format());
        } else {
            this.pw.println(inst.format(map));
        }
    }

    public void close() {
        /**
         * Closes the assembly writer
         */
        this.pw.close();
    }

}
