JAVAC := javac
JAVACC := javacc

default: compiler

compiler: scan.jar
	chmod u+x compile

scan.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=ParserGenerator ParserGenerator/scanner.jj
	$(JAVAC) LexicalAnalysis/*.java
	jar cfe $@ LexicalAnalysis.Scan */*.class

clean:
	rm -f scan.jar
	rm -f ParserGenerator/*.class
	rm -f LexicalAnalysis/*.class