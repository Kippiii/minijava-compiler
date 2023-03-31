package IRTranslation;

import SemanticChecking.Symbol.Symbol;
import syntax.*;
import tree.*;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

public class IRGenerator implements SyntaxTreeVisitor<Stm> {
    static final String printFunction = "print_int";
    static final String allocFunction = "alloc";
    Symbol curClassName;
    Symbol curMethodName;
    int curId;

    private Exp genVarLValue(Symbol variable) {
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
        return new BINOP(BINOP.PLUS, array, new BINOP(BINOP.MUL, offset, new CONST(4)));
    }

    private NameOfLabel genLabelWithIndex(String ... s) {
        s[s.length - 1] += Integer.toString(this.curId++);
        return new NameOfLabel(s);
    }

    private Exp genAllocation(Exp size) {
        return new CALL(new NameOfLabel(allocFunction), size);
    }

    public IRGenerator() {
        this.curClassName = null;
        this.curMethodName = null;
        this.curId = 0;
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
        return null;
    }

    public Stm visit(LocalDecl ld) {
        return null;
    }

    public Stm visit(FormalDecl fd) {
        return null;
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
            return new EVAL(new CONST(0));
        }
        Stm bTree = b.sl.get(b.sl.size() - 1).accept(this);
        for (int i = b.sl.size() - 2; i >= 0; i--) {
            bTree = new SEQ(b.sl.get(i).accept(this), bTree);
        }

        // Return
        return bTree;
    }

    public Stm visit(final If f) {
        // Create labels to be used
        NameOfLabel trueLabel = this.genLabelWithIndex("if", "else");
        NameOfLabel falseLabel = this.genLabelWithIndex("if", "else");
        NameOfLabel afterFalseLabel = this.genLabelWithIndex("if", "else");

        // Create conditional jump
        Exp cond = this.toExp(f.e.accept(this));
        Stm cjump = new CJUMP(CJUMP.NE, cond, new CONST(0), trueLabel, falseLabel);

        // Create statement (with label) for true
        Stm trueBody = f.s1.accept(this);
        Stm trueStm = new SEQ(new LABEL(trueLabel), new SEQ(trueBody, new JUMP(afterFalseLabel)));

        // Create statement (with label) for false
        Stm falseBody = f.s2.accept(this);
        Stm falseStm = new SEQ(new LABEL(falseLabel), new SEQ(falseBody, new LABEL(afterFalseLabel)));

        // Put all pieces together
        Stm ifStm = new SEQ(cjump, new SEQ(trueStm, falseStm));

        // Return statement
        return ifStm;
    }

    public Stm visit(final While w) {
        // Create labels to be used
        NameOfLabel loopLabel = this.genLabelWithIndex("while", "loop");
        NameOfLabel bodyLabel = this.genLabelWithIndex("while", "loop");
        NameOfLabel endLabel = this.genLabelWithIndex("while", "loop");

        // Create conditional jump to end
        Exp cond = this.toExp(w.e.accept(this));
        Stm cjump = new CJUMP(CJUMP.NE, cond, new CONST(0), bodyLabel, endLabel);

        // Create body of loop
        Stm body = w.s.accept(this);
        Stm bodyStm = new SEQ(new LABEL(bodyLabel), new SEQ(body, new LABEL(endLabel)));

        // Put all pieces together
        Stm whileStm = new SEQ(new LABEL(loopLabel), new SEQ(cjump, bodyStm));

        // Return statement
        return whileStm;
    }

    public Stm visit(final Print p) {
        // Find expression to be printed
        Exp toPrint = this.toExp(p.e.accept(this));

        // Create function call to "print" function
        Stm print = new EVAL(new CALL(new NameOfLabel(printFunction), toPrint));

        // Return call
        return print;
    }

    public Stm visit(final Assign a) {
        // Determine expression for l-value
        Exp lvalue = this.toExp(a.i.accept(this));

        // Determine expression for r-value
        Exp rvalue = this.toExp(a.e.accept(this));

        // Move r-value to l-value
        Stm move = new MOVE(lvalue, rvalue);

        // Return
        return move;
    }

    public Stm visit(ArrayAssign aa) {
        // Determine memory address of array
        Exp array = this.toExp(aa.nameOfArray.accept(this));

        // Get the l-value using offset
        Exp lvalue = this.getArrayOffsetLValue(array, this.toExp(aa.indexInArray.accept(this)));

        // Determine expression for r-value
        Exp rvalue = this.toExp(aa.e.accept(this));

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
        Exp result = new BINOP(BINOP.AND, left, right);

        // Return
        return this.toStm(result);
    }

    public Stm visit(final LessThan lt) {
        // Select temp variable for result (and labels)
        NameOfTemp ltTemp = new NameOfTemp("%lt");
        NameOfLabel trueLabel = new NameOfLabel("less", "than");
        NameOfLabel falseLabel = new NameOfLabel("less", "than");
        NameOfLabel afterFalseLabel = new NameOfLabel("less", "than");

        // Create true case
        Stm ifTrue = new SEQ(new LABEL(trueLabel), new MOVE(new TEMP(ltTemp), new CONST(1)));

        // Create false case
        Stm ifFalse = new SEQ(new LABEL(falseLabel), new MOVE(new TEMP(ltTemp), new CONST(0)));

        // Create a CJUMP
        Exp left = this.toExp(lt.e1.accept(this));
        Exp right = this.toExp(lt.e2.accept(this));
        Stm cjump = new CJUMP(CJUMP.LT, left, right, trueLabel, falseLabel);

        // Create expression for temp
        Exp e = new TEMP(ltTemp);

        // Return CEXP with statement and expression
        return this.toStm(new ESEQ(new SEQ(cjump, new SEQ(ifTrue, new SEQ(ifFalse, new LABEL(afterFalseLabel)))), e));
    }

    public Stm visit(final Plus p) {
        // Determine left-side expression
        Exp left = this.toExp(p.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(p.e2.accept(this));

        // Apply binop on both sides
        Exp result = new BINOP(BINOP.PLUS, left, right);

        // Return
        return this.toStm(result);
    }

    public Stm visit(final Minus m) {
        // Determine left-side expression
        Exp left = this.toExp(m.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(m.e2.accept(this));

        // Apply binop on both sides
        Exp result = new BINOP(BINOP.MINUS, left, right);

        // Return
        return this.toStm(result);
    }

    public Stm visit(final Times t) {
        // Determine left-side expression
        Exp left = this.toExp(t.e1.accept(this));

        // Determine right-side expression
        Exp right = this.toExp(t.e2.accept(this));

        // Apply binop on both sides
        Exp result = new BINOP(BINOP.MUL, left, right);

        // Return
        return this.toStm(result);
    }

    public Stm visit(final ArrayLookup al) {
        // Determine pointer to array
        Exp array = this.toExp(al.expressionForArray.accept(this));

        // Add offset to array pointer
        Exp lvalue = this.getArrayOffsetLValue(array, this.toExp(al.indexInArray.accept(this)));

        // Return value at pointer
        return this.toStm(new MEM(lvalue));
    }

    public Stm visit(final ArrayLength al) {
        // Determine pointer to array
        Exp array = this.toExp(al.expressionForArray.accept(this));

        // Return value at pointer
        return this.toStm(new MEM(array));
    }

    public Stm visit(Call c) {
        // TODO Find label of function
        NameOfLabel functionLabel = ?
        Exp f = new NAME(functionLabel);

        // Evaluate called-on object
        Exp callFrom = this.toExp(c.e.accept(this));

        // Evaluate parameters
        List<Exp> params = new ArrayList<Exp>();
        params.add(callFrom);
        for (Expression e : c.el) {
            params.add(this.toExp(e.accept(this)));
        }

        // Combine into call object
        Exp call = new CALL(f, params);

        // Return
        return this.toStm(call);
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
        return this.toStm(new MEM(lvalue));
    }

    public Stm visit(This t) {
        // Return first parameter
        return this.toStm(new TEMP("%i0"));
    }

    public Stm visit(NewArray na) {
        // Initialize temps
        NameOfTemp sizeTemp = new NameOfTemp("%na%size");
        NameOfTemp addrTemp = new NameOfTemp("%na%addr");

        // Get expression for size of array (including length)
        Stm getSize = new MOVE(new TEMP(sizeTemp), this.toExp(na.e.accept(this)));
        Exp sizePlusOne = new BINOP(BINOP.PLUS, new TEMP(sizeTemp), new CONST(1));
        Exp size = new BINOP(BINOP.MUL, sizePlusOne, new CONST(4));

        // Set up call for allocating size
        Exp addr = this.genAllocation(size);
        Stm getAddr = new MOVE(new TEMP(addrTemp), addr);

        // Initialize first element to be length
        Stm initLength = new MOVE(new MEM(new TEMP(addrTemp)), new TEMP(sizeTemp));

        // Return
        return this.toStm(new ESEQ(new SEQ(getSize, new SEQ(getAddr, initLength)), new TEMP(addrTemp)));
    }

    public Stm visit(NewObject no) {
        // TODO Calculate size of object
        int objSize = ?

        // Set up call for allocating size
        Exp allocation = this.genAllocation(new CONST(objSize));

        // Return
        return this.toStm(allocation);
    }

    public Stm visit(Not n) {
        // Evaluate expression
        Exp e = this.toExp(n.e.accept(this));

        // Set up binop (xor) with 1
        Exp call = new BINOP(BINOP.XOR, e, new CONST(1));

        // Return
        return this.toStm(call);
    }

    public Stm visit(Identifier id) {
        // Return l-value
        return this.toStm(this.genVarLValue(Symbol.symbol(id.s)));
    }

}
