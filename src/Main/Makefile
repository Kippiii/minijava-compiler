JAVAC := javac
JAVACC := javacc
SUPPORT := support.jar
GCC := sparc-linux-gcc

default: compiler

compiler: compile.jar runtime.o
	chmod u+x compile

compile.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
	$(JAVAC) -classpath .:$(SUPPORT) ParserGenerator/*.java
	$(JAVAC) LexicalAnalysis/*.java
	$(JAVAC) -classpath .:$(SUPPORT) Parsing/*.java
	$(JAVAC) -classpath .:$(SUPPORT) AbstractSyntax/*.java
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/*.java
	$(JAVAC) -classpath .:$(SUPPORT) SemanticChecking/Symbol/*.java
	$(JAVAC) -classpath .:$(SUPPORT) IRTranslation/*.java
	$(JAVAC) -classpath .:$(SUPPORT) InstructionSelection/*.java
	$(JAVAC) -classpath .:$(SUPPORT) RegisterAllocator/*.java
	$(JAVAC) -classpath .:$(SUPPORT) Main/*.java
	jar cfm $@ Manifest */*.class */*/*.class

runtime.o:
	$(GCC) -Wall -c runtime.c -o runtime.o

clean:
	rm -f compile.jar
	rm -f Main/*.class
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class
	rm -f Parsing/*.class
	rm -f AbstractSyntax/*.class
	rm -f SemanticChecking/*.class
	rm -f SemanticChecking/Symbol/*.class
	rm -f IRTranslation/*.class
	rm -f InstructionSelection/*.class
	rm -f RegisterAllocator/*.class