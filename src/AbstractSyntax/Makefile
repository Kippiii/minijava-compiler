JAVAC := javac
JAVACC := javacc
SUPPORT := support.jar

default: compiler

compiler: check.jar
	chmod u+x compile

check.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
	$(JAVAC) -classpath .:$(SUPPORT) ParserGenerator/*.java
	$(JAVAC) LexicalAnalysis/*.java
	$(JAVAC) -classpath .:$(SUPPORT) Parsing/*.java
	$(JAVAC) -classpath .:$(SUPPORT) AbstractSyntax/*.java
	jar cfm $@ Manifest */*.class

clean:
	rm -f check.jar
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class
	rm -f Parsing/*.class
	rm -f AbstractSyntax/*.class