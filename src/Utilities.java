import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Utilities {
    /**
     * Contains information needed for running the compiler and converting it into a jar file
     */

    public enum Phase {
        /**
         * Represents the "phase" of the compiler being run or jarred. Useful for submitting and debugging.
         */
        LEXER,
        PARSER,
        ABSTRACT_BUILDER,
        CHECKER,
        TRANSLATOR,
        SELECTOR,
        ALLOCATOR,
        MAIN,
    }
    public static Phase getPhase(String phaseStr) {
        /**
         * Converts a string to the corresponding phase object.
         * @param phaseStr - The user-inputted string
         * @return The corresponding phase object
         */
        if (phaseStr.equals("lexer"))
            return Phase.LEXER;
        if (phaseStr.equals("parser"))
            return Phase.PARSER;
        if (phaseStr.equals("abstract"))
            return Phase.ABSTRACT_BUILDER;
        if (phaseStr.equals("checker"))
            return Phase.CHECKER;
        if (phaseStr.equals("translate"))
            return Phase.TRANSLATOR;
        if (phaseStr.equals("selector"))
            return Phase.SELECTOR;
        if (phaseStr.equals("allocate"))
            return Phase.ALLOCATOR;
        return Phase.MAIN;
    }
    public static String getDirectory(Phase phase) {
        /**
         * Gets the directory with the main files for a given phase
         * @param phase - The phase of the compiler being ran or jarred
         * @return The string of the directory where the main file for the phase is stored
         */
        switch(phase) {
            case LEXER:
                return "LexicalAnalysis";
            case PARSER:
                return "Parsing";
            case ABSTRACT_BUILDER:
                return "AbstractSyntax";
            case CHECKER:
                return "SemanticChecking";
            case TRANSLATOR:
                return "IRTranslation";
            case SELECTOR:
                return "InstructionSelection";
            case ALLOCATOR:
                return "RegisterAllocator";
            case MAIN:
                return "Main";
        }
        return "";
    }
    public static List<String> getDirectoriesToAdd(Phase phase) {
        /**
         * Determines which files to add to the jar file for a given phase
         * @param phase - The phase of the compiler being ran or jarred
         * @return The list of files to add into the jar file
         */
        List<String> directories = new ArrayList<String>();
        switch(phase) {
            case MAIN:
                directories.add("Main/Main.java");
            case ALLOCATOR:
                directories.add("RegisterAllocator/Allocate.java");
                directories.add("RegisterAllocator/AssemFlowGraph.java");
                directories.add("RegisterAllocator/AssemInterferenceGraph.java");
            case SELECTOR:
                directories.add("InstructionSelection/Select.java");
                directories.add("InstructionSelection/Codegen.java");
                directories.add("InstructionSelection/IRParseException.java");
                directories.add("InstructionSelection/AssemblyWriter.java");
            case TRANSLATOR:
                directories.add("IRTranslation/Translate.java");
                directories.add("IRTranslation/IRGenerator.java");
                directories.add("IRTranslation/TranslateReturn.java");
            case CHECKER:
                directories.add("SemanticChecking/Symbol/BasicType.java");
                directories.add("SemanticChecking/Symbol/ClassType.java");
                directories.add("SemanticChecking/Symbol/MethodType.java");
                directories.add("SemanticChecking/Symbol/Symbol.java");
                directories.add("SemanticChecking/Symbol/Type.java");
                directories.add("SemanticChecking/Symbol/NameSpace.java");
                directories.add("SemanticChecking/Check.java");
                directories.add("SemanticChecking/CheckReturn.java");
                directories.add("SemanticChecking/NameConflictError.java");
                directories.add("SemanticChecking/SymbolTableFactory.java");
                directories.add("SemanticChecking/AccessingNonClassError.java");
                directories.add("SemanticChecking/InvalidClassError.java");
                directories.add("SemanticChecking/InvalidNumArgsError.java");
                directories.add("SemanticChecking/MethodNotFoundError.java");
                directories.add("SemanticChecking/TypeChecker.java");
                directories.add("SemanticChecking/TypeMismatchError.java");
                directories.add("SemanticChecking/UndefinedSymbolError.java");
                directories.add("SemanticChecking/CyclicExtensionError.java");
            case ABSTRACT_BUILDER:
                directories.add("AbstractSyntax/BuildAbstract.java");
                directories.add("AbstractSyntax/TreePrinter.java");
                directories.add("Manifest");
            case PARSER:
                directories.add("Parsing/Parse.java");
                directories.add("Parsing/MyParseException.java");
            case LEXER:
                directories.add("LexicalAnalysis/Scan.java");
                directories.add("LexicalAnalysis/LexException.java");
            default:
                directories.add("ParserGenerator/scanner.jj");
                directories.add("ErrorManagement/CompilerException.java");
                directories.add("ErrorManagement/CompilerExceptionList.java");
                directories.add("ErrorManagement/UnexpectedException.java");
        }
        return directories;
    }
    public static void copyFile(String s1, String s2) throws IOException {
        /**
         * Copies a file from one path to another
         * @param s1 - The path to the source file
         * @param s2 - The path to the destination file
         * @throws IOException
         */
        Path fromPath = Paths.get(s1);
        Path toPath = Paths.get(s2);
        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
    }
    public static void runCommand(String cmd) throws IOException {
        /**
         * Runs a command in the shell on the current system
         * @param cmd - The command to be run
         * @throws IOException
         */
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

    public static String getJarName(Phase phase) {
        /**
         * Gets the name of the jar file to create for a given phase
         * @param phase - The phase of the compiler being ran or jarred
         * @return The name of the jar file to create
         */
        switch (phase) {
            case LEXER:
                return "phase02.jar";
            case PARSER:
                return "phase03.jar";
            case ABSTRACT_BUILDER:
                return "phase04.jar";
            case CHECKER:
                return "phase05.jar";
            case TRANSLATOR:
                return "phase07.jar";
            case SELECTOR:
                return "phase09.jar";
            case ALLOCATOR:
                return "phase11.jar";
            case MAIN:
                return "phase12.jar";
        }
        return "invalid.jar";
    }

}
