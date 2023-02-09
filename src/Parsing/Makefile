JAVAC := javac
JAVACC := javacc

default: compiler

compiler: parse.jar
	chmod u+x compile

parse.jar:
	$(JAVACC) -STATIC=false -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
	$(JAVAC) ParserGenerator/*.java
	$(JAVAC) LexicalAnalysis/*.java
	$(JAVAC) Parsing/*.java
	jar cfe $@ Parsing.Parse */*.class

clean:
	rm -f parse.jar
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class
	rm -f Parsing/*.class