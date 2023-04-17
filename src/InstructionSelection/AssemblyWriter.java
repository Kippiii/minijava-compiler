package InstructionSelection;

import assem.Instruction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AssemblyWriter {
    private PrintWriter pw;

    public AssemblyWriter(String filename) throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename));
    }

    public void writeInstruction(Instruction inst) {
        this.pw.println(inst.format());
    }

    public void close() {
        this.pw.close();
    }

}
