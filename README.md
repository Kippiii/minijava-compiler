# Mini Java Compiler

This is a compiler for mini Java (a reduced Java syntax) that compiles mini Java programs
to the SPARC assembly. This code was created using the textbook [Modern Compiler Implementation in Java, second edition](https://www.cambridge.org/us/academic/subjects/computer-science/programming-languages-and-applied-logic/modern-compiler-implementation-java-2nd-edition?format=HB&isbn=9780521820608)
by [Andrew W. Appel](https://www.cs.princeton.edu/~appel/).

## How to Use
This compiler outputs to SPARC assembly, so the user will need to emulate SPARC (or have a
SPARC machine) in order to assemble and run the output of this compiler. This document will
not explain how to do this, but feel free to look at [Jabberwocky](https://github.com/Kippiii/Jabberwocky).

To run the compiler from this repo, run the following command:
```bash
javac Utilities.java
java Runner.java [source_file_path] [phase] (debug)?
```
The `source_file_path` is the file that will be compiled. Enter `debug` if you would like
debug output. The `phase` is the phase of the compiler you would like to run. The options
for this are:
* [lexer](src/LexicalAnalysis)
* [parser](src/Parsing)
* [abstract](src/AbstractSyntax)
* [checker](src/SemanticChecking)
* [translate](src/IRTranslation)
* [selector](src/InstructionSelection)
* [allocate](src/RegisterAllocator)
* [main](src/Main)

Another option for running the compiler is to output it to a jar file that contains
everything needed to run the phase of the compiler. Here is how to do that:
```bash
javac Utilities.java
java Jarrer.java [phase]
```
This will output a file called `phase[i].jar` (the `i` depends on what phase you jar). To
run the compiler from this jar, do the following:
```bash
jar -xf phase[i].jar
make
./compile [source_file_path].java
# Only do if in Main phase
./assemble [source_file_path].s
./[source_file_path]
```

## Phases
This compiler constists of many phases that are run linearly (from top one on). If you
run a phase, it will run itself and all previous phases. What follows is a list of phases
along with some information about each one:
### Lexical Analysis
Checks over the lexical structure of the source program. Converts the ASCII text into
tokens. Throws errors if it cannot make at token at a point in the program. All tokens
are derived from the Java language manual. Thus, any valid Java program should pass this
phase of analysis.
### Parsing
Applies grammar rules on the stream of tokens in order to create a parsing tree. The
grammar used for this is a reduced grammar of Java called mini Java. The grammar was
created in JavaCC, and it is LL(2) (although it should probably be made LL(1)). The
grammar specification can be found [here](EBNF%20Grammar%20for%20Mini-Java.html) (credit
to [Dr. Ryan Stansifer](https://cs.fit.edu/~ryan/)) for creating this grammar.
### Abstract Syntax
Converts the parsing tree into an abstract syntax tree.
### Semantic Checking
Traverses the abstract syntax tree to create a symbol table and to check for semantic
errors. The following semantic errors are checked:
* Accessing a non-class variable
* Cyclic inheritance
* Unknown class error
* Invalid argument number in method call
* Name conflict error
* Type mismatch error
* Undefined symbol error
### IR Translation
Converts the abstract syntax tree to an intermediate represenation.
### Instruction Selection
Selects SPARC assembly instructions based on IR code.
### Register Allocation
Maps temporary values to registers using graph coloring.
### Main
Runs the entire compiler all together (runs each phase).

## Future Ideas
### Front End
* Transition from JavaCC to Lex and Yacc (more standard)
* Implement more common Java constructions
  * For loops
  * More boolean operators
  * More comparison operators
  * Bit shift operators
  * Switch statements
  * Do-while loops
* Add more data types
  * Floats
  * Strings
* Allow more complicated programs
  * Return statements not at end of method
  * More return types of methods
  * If statements without else clauses
* Declarations in the middle of blocks (with scope)
* Allow more than five arguments
* Check for uninitialized variables
* Add abstract Frame data structure
* Implement more complex IR translator from book
### Back End
* Output to another architecture (MIPS?)
* Implement own canonicalization (rather than relying on book)
* Use dynamic programming instead of maximal munch of instruction selection
* Add instructions to abstract classes and use that for scalability
* Have some registers be known colors but not able to have temporaries colored to
* Add register spilling
* Add coalescing to eliminate MOVEs