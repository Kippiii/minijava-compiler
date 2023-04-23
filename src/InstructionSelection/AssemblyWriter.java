package InstructionSelection;

import assem.Instruction;
import tree.NameOfTemp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AssemblyWriter {
    private PrintWriter pw;

    public AssemblyWriter(String filename) throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename));
    }

    public void writeInstruction(Instruction inst) {
        this.writeInstruction(inst, null);
    }

    public void writeInstruction(Instruction inst, Map<NameOfTemp, String> map) {
        if (map == null) {
            this.pw.println(inst.format());
        } else {
            this.pw.println(inst.format(map));
        }
    }

    public void close() {
        this.pw.close();
    }

}
