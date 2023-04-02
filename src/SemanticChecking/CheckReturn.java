package SemanticChecking;

import SemanticChecking.Symbol.NameSpace;
import syntax.Program;

public record CheckReturn(Program syntaxTree, NameSpace symbolTable) {}
