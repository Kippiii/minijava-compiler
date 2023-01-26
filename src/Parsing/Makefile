JAVAC := javac
JAVACC := javacc

default: compiler

compiler: parse.jar
	chmod u+x compile

parse.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/scanner.jj
	$(JAVAC) */*.java
	jar cfe $@ main.Parse */*.class

clean:
	rm parse.jar
	rm */*.class