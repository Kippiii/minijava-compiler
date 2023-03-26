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
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/*.java
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/Symbol/*.java
	cp $(SUPPORT) ./support.jar || exit 0
	jar cfm $@ Manifest */*.class */*/*.class

clean:
	rm -f check.jar
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class
	rm -f Parsing/*.class
	rm -f AbstractSyntax/*.class
	rm -f SemanticChecking/*.class