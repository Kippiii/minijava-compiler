import java.io.IOException;
import java.util.List;

public class Jarrer {

    public static void main(String[] args) throws IOException {
        /**
         * Adds files of the compiler into a jar file to be submitted for assignment
         * @throws IOException
         */
        if (args.length == 1) {
            Utilities.Phase phase = Utilities.getPhase(args[0]);

            // Copy Makefile and compile into home directory
            String dir = Utilities.getDirectory(phase);
            Utilities.copyFile(dir + "/Makefile", "Makefile");
            Utilities.copyFile(dir + "/compile", "compile");
            Utilities.copyFile(dir + "/Manifest", "Manifest");
            if (phase == Utilities.Phase.MAIN) {
                Utilities.copyFile(dir + "/assemble", "assemble");
                Utilities.copyFile(dir + "/runtime.c", "runtime.c");
                Utilities.copyFile(dir + "/README", "README");
                Utilities.copyFile(dir + "/../TestFiles/MyTest1.java", "MyTest1.java");
                Utilities.copyFile(dir + "/../TestFiles/MyTest2.java", "MyTest2.java");
            }

            // Getting files to add to jar
            List<String> filesToAdd = Utilities.getDirectoriesToAdd(phase);
            String files = filesToAdd.get(0);
            for (int i = 1; i < filesToAdd.size(); i++)
                files += " " + filesToAdd.get(i);
            files += " Makefile compile";
            files += " Manifest";
            if (phase == Utilities.Phase.MAIN) {
                files += " assemble runtime.c README MyTest1.java MyTest2.java";
            }
            String jarFile = Utilities.getJarName(phase);
            Utilities.runCommand("jar cvf " + jarFile + " " + files);
        }
    }

}
