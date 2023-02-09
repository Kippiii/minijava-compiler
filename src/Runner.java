import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Runner {
    enum Phase {
        LEXER,
        PARSER
    }
    static Phase getPhase(String phaseStr) {
        if (phaseStr.equals("lexer"))
            return Phase.LEXER;
        if (phaseStr.equals("parser"))
            return Phase.PARSER;
        return Phase.PARSER;
    }
    static String getDirectory(Phase phase) {
        switch(phase) {
            case LEXER:
                return "LexicalAnalysis";
            case PARSER:
                return "Parsing";
        }
        return "";
    }
    static void copyFile(String s1, String s2) throws IOException {
        Path fromPath = Paths.get(s1);
        Path toPath = Paths.get(s2);
        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
    }
    static void runCommand(String cmd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(cmd.split("\\s+")));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            System.out.println(line);
        }
    }
    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            String fileName = args[0];
            Phase phase = getPhase(args[1]);

            // Copy Makefile and compile into home directory
            String dir = getDirectory(phase);
            copyFile(dir + "/Makefile", "Makefile");
            copyFile(dir + "/compile", "compile");

            // Run Makefile and compile script
            runCommand("make clean");
            runCommand("make");
            runCommand("./compile " + fileName);
        }
    }
}
