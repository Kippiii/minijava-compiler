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

    private Exp toExp(Stm s) {
        assert(s instanceof EVAL);
        return ((EVAL) s).exp;
    }

    private Stm toStm(Exp e) {
        return new EVAL(e);
    }

    private Exp getArrayOffsetLValue(Exp array, Exp offset) {
        // TODO
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
        // Create sequence of each child statement
        if (b.sl.size() == 0) {
            // TODO
        }
        Stm bTree = b.sl.get(b.sl.size() - 1).accept(this);
        for (int i = b.sl.size() - 2; i >= 0; i--) {
            bTree = new SEQ(b.sl.get(i).accept(this), bTree);
        }

        // Return
        return bTree;
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
        // Determine expression for l-value
        Exp lvalue = this.toExp(a.i.accept(this));

        // Determine expression for r-value
        Exp rvalue = this.toExp(a.e.accepth(this));

        // Move r-value to l-value
        Stm move = new MOVE(lvalue, rvalue);

        // Return
        return move;
    }

    public Stm visit(ArrayAssign aa) {
        // Determine memory address of array
        Exp array = aa.nameOfArray.accept(this);

        // Get the l-value using offset
        Exp lvalue = this.getArrayOffsetLValue(array, aa.indexInArray);

        // Determine expression for r-value
        Exp rvalue = aa.e.accept(this);

        // Move r-value to l-value
        Stm move = new MOVE(lvalue, rvalue);

        // Return
        return move;
    }

    public Stm visit(final And a) {
        // Determine left-side expression
        Exp left = this.toExp(a.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(a.e2.accept(this));

        // Apply binop and to both sides
        Stm result = new BINOP(BINOP.AND, left, right);

        // Return
        return result;
    }

    public Stm visit(final LessThan lt) {
        // TODO
        // Select temp variable for result

        // Create true case

        // Create false case

        // Create a CJUMP

        // Create expression for temp

        // Return CEXP with statement and expression
    }

    public Stm visit(final Plus p) {
        // Determine left-side expression
        Exp left = this.toExp(p.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(p.e2.accept(this));

        // Apply binop on both sides
        Stm result = new BINOP(BINOP.PLUS, left, right);

        // Return
        return result;
    }

    public Stm visit(final Minus m) {
        // Determine left-side expression
        Exp left = this.toExp(m.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(m.e2.accept(this));

        // Apply binop on both sides
        Stm result = new BINOP(BINOP.MINUS, left, right);

        // Return
        return result;
    }

    public Stm visit(final Times t) {
        // Determine left-side expression
        Exp left = this.toExp(t.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(t.e2.accept(this));

        // Apply binop on both sides
        Stm result = new BINOP(BINOP.MUL, left, right);

        // Return
        return result;
    }

    public Stm visit(final ArrayLookup al) {
        // Determine pointer to array
        Exp array = this.toExp(al.expressionForArray.accept(this));

        // Add offset to array pointer
        Exp lvalue = this.getArrayOffsetLValue(array, al.indexInArray.accept(this));

        // Return value at pointer
        return new MEM(lvalue);
    }

    public Stm visit(final ArrayLength al) {
        // Determine pointer to array
        Exp array = this.genVarLValue(Symbol.symbol(al.expressionForArray));

        // Return value at pointer
        return new MEM(array);
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
        return this.toStm(new CONST(1));
    }

    public Stm visit(False f) {
        return this.toStm(new CONST(0));
    }

    public Stm visit(IntegerLiteral il) {
        return this.toStm(new CONST(il.i));
    }

    public Stm visit(IdentifierExp ie) {
        // Get identifier l-value
        Exp lvalue = this.genVarLValue(Symbol.symbol(ie.s));

        // Return computed r-value
        return new MEM(lvalue);
    }

    public Stm visit(This t) {
        // Return first parameter
        return new TEMP("%i0");
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
        // Evaluate expression
        Exp e = n.e.accept(this);

        // Set up binop (xor) with 1
        Stm call = new BINOP(BINOP.XOR, e, new CONST(1));

        // Return
        return call;
    }

    public Stm visit(Identifier id) {
        // Return l-value
        return this.genVarLValue(Symbol.symbol(id.s));
    }

}
