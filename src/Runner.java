import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            String fileName = args[0];
            Utilities.Phase phase = Utilities.getPhase(args[1]);
            boolean debug = args.length >= 3 && args[2].equals("debug");

            // Copy Makefile and compile into home directory
            String dir = Utilities.getDirectory(phase);
            Utilities.copyFile(dir + "/Makefile", "Makefile");
            Utilities.copyFile(dir + "/compile", "compile");
            if (phase == Utilities.Phase.ABSTRACT_BUILDER || phase == Utilities.Phase.CHECKER)
                Utilities.copyFile(dir + "/Manifest", "Manifest");

            // Run Makefile and compile script
            Utilities.runCommand("make clean");
            Utilities.runCommand("make");
            Utilities.runCommand("./compile " + fileName + (debug ? " debug" : ""));
        }
    }
}
