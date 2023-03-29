package IRTranslation;

import SemanticChecking.Symbol.Symbol;
import syntax.*;
import tree.*;

public class IRGenerator implements SyntaxTreeVisitor<Stm> {
    Symbol curClassName;
    Symbol curMethodName;

    private genVarLValue(Symbol variable) {
        // TODO
        // If variable is variable
            // Return register
        // If variable is local
            // Return stack offset
        // If variable is class
            // Return access of this
        // Error
    }

    public IRGenerator() {
        this.curClassName = null;
        this.curMethodName = null;
    }

    public Stm visit(Program p) {
        p.m.accept(this);
        for (ClassDecl cd : p.cl) {
            cd.accept(this);
        }
        return null;
    }

    public Stm visit(MainClass mc) {
        // TODO save class name
        // TODO special class decl
        mc.body.accept(this);
        return null;
    }

    public Stm visit(SimpleClassDecl cd) {
        // TODO save class name
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        return null;
    }

    public Stm visit(ExtendingClassDecl cd) {
        // TODO save class name
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        return null;
    }

    public Stm visit(MethodDecl md) {
        // TODO
        // Create label for function

        // Create function body

        // Move return value into register

        // Create jump to epilogue

        // Put pieces together

        // Add to list of functions
    }

    public Stm visit(FieldDecl fd) {
        // TODO
    }

    public Stm visit(LocalDecl ld) {
        // TODO
    }

    public Stm visit(FormalDecl fd) {
        // TODO
    }

    public Stm visit(IntArrayType iat) {
        return null;
    }

    public Stm visit(BooleanType bt) {
        return null;
    }

    public Stm visit(IntegerType it) {
        return null;
    }

    public Stm visit(VoidType vt) {
        return null;
    }

    public Stm visit(IdentifierType it) {
        return null;
    }

    public Stm visit(final Block b) {
        // TODO
        // Create sequence of each child statement

        // Return
    }

    public Stm visit(final If f) {
        // TODO
        // Create labels to be used

        // Create conditional jump

        // Create statement (with label) for true

        // Create statement (with label) for false

        // Put all pieces together

        // Return statement
    }

    public Stm visit(final While w) {
        // TODO
        // Create labels to be used

        // Create conditional jump to end

        // Create body of loop

        // Put all pieces together

        // Return statement
    }

    public Stm visit(final Print p) {
        // TODO
        // Find expression to be printed

        // Create function call to "print" function

        // Return call
    }

    public Stm visit(final Assign a) {
        // TODO
        // Determine expression for l-value

        // Determine expression for r-value

        // Move r-value to l-value

        // Return
    }

    public Stm visit(ArrayAssign aa) {
        // TODO
        // Determine memory address of array

        // Get the l-value using offset

        // Determine expression for r-value

        // Move r-value to l-value

        // Return
    }

    public Stm visit(final And a) {
        // TODO
        // Determine left-side expression

        // Determine right-side expression

        // Apply binop and to both sides

        // Return
    }

    public Stm visit(final LessThan lt) {
        // TODO How less than?
    }

    public Stm visit(final Plus p) {
        // TODO
        // Determine left-side expression

        // Determine right-side expression

        // Apply binop on both sides

        // Return
    }

    public Stm visit(final Minus m) {
        // TODO
        // Determine left-side expression

        // Determine right-side expression

        // Apply binop on both sides

        // Return
    }

    public Stm visit(final Times t) {
        // TODO
        // Determine left-side expression

        // Determine right-side expression

        // Apply binop on both sides

        // Return
    }

    public Stm visit(final ArrayLookup al) {
        // TODO
        // Determine pointer to array

        // Add offset to array pointer

        // Return value at pointer
    }

    public Stm visit(final ArrayLength al) {
        // TODO
        // Determine pointer to array

        // Return value at pointer
    }

    public Stm visit(Call c) {
        // TODO
        // Find label of function

        // Evaluate called-on object

        // Evaluate parameters

        // Combine into call object

        // Return
    }

    public Stm visit(True t) {
        // TODO
        // Return constant 1
    }

    public Stm visit(False f) {
        // TODO
        // Return constant 0
    }

    public Stm visit(IntegerLiteral il) {
        // TODO
        // Return integer literal
    }

    public Stm visit(IdentifierExp ie) {
        // TODO
        // Get identifier l-value

        // Return computed r-value
    }

    public Stm visit(This t) {
        // TODO
        // Return first parameter
    }

    public Stm visit(NewArray na) {
        // TODO
        // Get expression for size of array (including length)

        // Set up call for allocating size

        // Initialize first element to be length

        // Return
    }

    public Stm visit(NewObject no) {
        // TODO
        // Calculate size of object

        // Set up call for allocating size

        // Return
    }

    public Stm visit(Not n) {
        // TODO
        // Evaluate expression

        // Set up binop (xor) with 1

        // Return
    }

    public Stm visit(Identifier id) {
        // TODO
        // Return l-value
    }

}
