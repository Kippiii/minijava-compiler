package IRTranslation;

import ErrorManagement.CompilerException;
import ErrorManagement.CompilerExceptionList;
import SemanticChecking.Check;
import SemanticChecking.CheckReturn;
import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import syntax.Program;
import tree.Stm;
import tree.TreePrint;

import java.io.FileNotFoundException;
import java.util.Map;

public class Translate {
    static String filename = "Factorial.java";

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length >= 1)
            filename = args[0];
        boolean debug = args.length >= 2 && args[1].equals("debug");

        int errors = 0;
        try {
            translate(filename, debug);
        } catch (CompilerExceptionList exc) {
            for (CompilerException err : exc) {
                System.err.printf("%s:%s%n", filename, err.toString());
                errors++;
            }
        }

        System.out.printf("filename=%s, errors=%d%n", filename, errors);
    }

    public static TranslateReturn translate(String filename) throws FileNotFoundException, CompilerExceptionList {
        return translate(filename, false);
    }

    public static TranslateReturn translate(String filename, boolean debug) throws FileNotFoundException, CompilerExceptionList {
        CheckReturn checked = Check.check(filename);
        Program syntaxTree = checked.syntaxTree();
        NameSpace symbolTable = checked.symbolTable();

        IRGenerator ir = new IRGenerator(symbolTable);
        ir.visit(syntaxTree);
        Map<Symbol, Stm> fragments = ir.fragments;

        if (debug) {
            for (Map.Entry<Symbol, Stm> entry : fragments.entrySet()) {
                System.out.printf("!  Procedure fragment %s%n", entry.getKey().toString());
                System.out.print(TreePrint.toString(entry.getValue()));
                System.out.println("!  End fragment");
                System.out.println();
            }
        }

        return new TranslateReturn(fragments, symbolTable);
    }
}
