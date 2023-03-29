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

    public enum Phase {
        LEXER,
        PARSER,
        ABSTRACT_BUILDER,
        CHECKER,
        TRANSLATOR,
    }
    public static Phase getPhase(String phaseStr) {
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
        return Phase.TRANSLATOR;
    }
    public static String getDirectory(Phase phase) {
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
                return "IRTranslation"
        }
        return "";
    }
    public static List<String> getDirectoriesToAdd(Phase phase) {
        List<String> directories = new ArrayList<String>();
        switch(phase) {
            case TRANSLATOR:
            case CHECKER:
                directories.add("SemanticChecking/Symbol/BasicType.java");
                directories.add("SemanticChecking/Symbol/ClassType.java");
                directories.add("SemanticChecking/Symbol/MethodType.java");
                directories.add("SemanticChecking/Symbol/Symbol.java");
                directories.add("SemanticChecking/Symbol/Type.java");
                directories.add("SemanticChecking/Check.java");
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
        }
        return directories;
    }
    public static void copyFile(String s1, String s2) throws IOException {
        Path fromPath = Paths.get(s1);
        Path toPath = Paths.get(s2);
        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
    }
    public static void runCommand(String cmd) throws IOException {
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
        }
        return "invalid.jar";
    }

}
