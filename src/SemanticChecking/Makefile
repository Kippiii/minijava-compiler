.PHONY default compiler

JAVAC := javac
JAVACC := javacc

default: compiler

compiler: check.jar
    chmod u+x compile

check.jar:
    $(JAVACC) -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
    $(JAVAC) LexicalAnalysis/*.java
    $(JAVAC) Parsing/*java
    $(JAVAC) -classpath .:$SUPPORT AbstractSyntax/*.java
    $(JAVAC) -classpath .:$SUPPORT SemanticChecking/*.java
    jar cfme $@ Manifest SemanticChecking.Check */*.class

clean:
    rm -f check.jar
    rm -f ParserGenerator/*.class
    rm -f LexicalAnalysis/*.class
    rm -f Parsing/*.class
    rm -f AbstractSyntax/*.class
    rm -f SemanticChecking/*.class