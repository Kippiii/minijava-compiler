JAVAC := javac
GCC := sparc-linux-gcc

default :  compiler

compiler :  Main.class runtime.o compile
	chmod u+x compile

Main.class :  Main.java
	$(JAVAC) -encoding ASCII -Xlint -Xlint:-serial Main.java

runtime.o :  runtime.c
	$(GCC) -Wall -c runtime.c -o runtime.o