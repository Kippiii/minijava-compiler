package IRTranslation;

import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import tree.Stm;

import java.util.Map;

public record TranslateReturn(Map<Symbol, Stm> fragments, NameSpace symbolTable) {}
