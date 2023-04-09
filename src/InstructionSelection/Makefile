JAVAC := javac
JAVACC := javacc
SUPPORT := support.jar

default: compiler

compiler: select.jar
	chmod u+x compile

select.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
	$(JAVAC) -classpath .:$(SUPPORT) ParserGenerator/*.java
	$(JAVAC) LexicalAnalysis/*.java
	$(JAVAC) -classpath .:$(SUPPORT) Parsing/*.java
	$(JAVAC) -classpath .:$(SUPPORT) AbstractSyntax/*.java
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/*.java
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/Symbol/*.java
	$(JAVAC) -classpath .:$(SUPPORT) IRTranslation/*.java
	$(JAVAC) -classpath .:$(SUPPORT) InstructionSelection/*.java
	jar cfm $@ Manifest */*.class */*/*.class

clean:
	rm -f select.jar
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class
	rm -f Parsing/*.class
	rm -f AbstractSyntax/*.class
	rm -f SemanticChecking/*.class
	rm -f SemanticChecking/Symbol/*.class
	rm -f IRTranslation/*.class
	rm -f InstructionSelection/*.class