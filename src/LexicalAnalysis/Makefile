JAVAC := javac
JAVACC := javacc

default: compiler

compiler: scan.jar
	chmod u+x compile

scan.jar:
	$(JAVACC) -OUTPUT_DIRECTORY=parser parser/scanner.jj
	$(JAVAC) */*.java
	jar cfe $@ main.Scan */*.class

clean:
	rm scan.jar
	rm */*.class