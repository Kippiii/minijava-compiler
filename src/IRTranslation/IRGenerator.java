package IRTranslation;

import SemanticChecking.Symbol.ClassType;
import SemanticChecking.Symbol.MethodType;
import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import syntax.*;
import tree.*;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRGenerator implements SyntaxTreeVisitor<Stm> {
    /**
     * Visitor that converts an abstract syntax tree to IR code
     * @param printFunction - The name of the helper function used for printing
     * @param allocFunction - The name of the helper function used for allocating memory
     * @param curClassName - The name of the current class being explored
     * @param curMethodName - The name of the current method being explored
     * @param curId - The current id to be appended to temp names
     * @param symbolTable - The symbol table corresponding to the source program
     * @param fragments - A mapping of method names to IR code (the output)
     */
    static final String printFunction = "print_int";
    static final String allocFunction = "alloc";
    Symbol curClassName;
    Symbol curMethodName;
    int curId;
    NameSpace symbolTable;
    Map<Symbol, Stm> fragments;

    private Exp genVarLValue(Symbol variable) {
        /**
         * Generates the IR code to get the l-value of a variable
         * @param variable - The name of the variable
         * @return The IR code that gets the l-value of the variable
         */
        ClassType ct = (ClassType) this.symbolTable.getType(this.curClassName);
        MethodType mt = ct.getMethodType(this.curMethodName);

        // If variable is arg
        if (mt.getArgType(variable) != null) {
            // Return register
            int argNum = mt.getArgNum(variable) + 1;
            return new TEMP("%i" + Integer.toString(argNum));
        }

        // If variable is local
        if (mt.getLocalType(variable) != null) {
            // Return stack offset
            int offset = 4*mt.getLocalNum(variable) + 4;
            return new BINOP(BINOP.MINUS, new TEMP("%fp"), new CONST(offset));
        }

        // Variable is field
        while (true) {
            if (ct.getFieldType(variable) != null) {
                // Return access of this
                int offset = ct.getOffset(variable, this.symbolTable);
                return new BINOP(BINOP.PLUS, new TEMP("%i0"), new CONST(offset));
            }
            if (ct.getExtName() == null) {
                break;
            }
            ct = (ClassType) this.symbolTable.getType(ct.getExtName());
        }
        return null;
    }

    private Exp toExp(Stm s) {
        /**
         * Converts a statement to an expression
         * @param s - The statement
         * @return The related expression
         */
        assert(s instanceof EVAL);
        return ((EVAL) s).exp;
    }

    private Stm toStm(Exp e) {
        /**
         * Converts an expression to a statement
         * @param e - The expression
         * @return The related statement
         */
        return new EVAL(e);
    }

    private Exp getArrayOffsetLValue(Exp array, Exp offset) {
        /**
         * Creates IR code that gets the l-value of an array offset (for accessing)
         * @param array - Expression for the address of the array
         * @param offset - Expression for the offset of the array to get
         * @return Expression for the address of that offset of the array
         */
        return new BINOP(BINOP.PLUS, array, new BINOP(BINOP.MUL, new BINOP(BINOP.PLUS, offset, new CONST(1)), new CONST(4)));
    }

    private NameOfLabel genLabelWithIndex(String ... s) {
        /**
         * Creates a label with an index at the end
         * @param s - List of strings to concatenate
         * @return The generated label
         */
        s[s.length - 1] = s[s.length - 1] + Integer.toString(this.curId++);
        return new NameOfLabel(s);
    }

    private Exp genAllocation(Exp size) {
        /**
         * Generates IR code that allocates memory of the given size and returns its address
         * @param size - An expression representing the size to allocate
         * @return An expression that allocates the memory and returns the address
         */
        return new CALL(new NameOfLabel(allocFunction), size);
    }

    private String getNameForMethod(String className, String methodName) {
        /**
         * Gets the label to jump to for a particular method call
         * @param className - The name of the class whose instance is getting called from
         * @param methodName - The name of the method being called
         * @return The string version of the label to jump to
         */
        ClassType ct = (ClassType) this.symbolTable.getType(Symbol.symbol(className));
        while (true) {
            if (ct.getMethodType(Symbol.symbol(methodName)) != null) {
                return ct.getName();
            }
            if (ct.getExtName() == null) {
                break;
            }
            ct = (ClassType) this.symbolTable.getType(ct.getExtName());
        }
        return null;
    }

    private Exp toRValue(Exp lValue) {
        /**
         * Converts an l-value to an r-value
         * @param lValue - Expression representing the l-value of a variable
         * @return The r-value related to the l-value
         */
        if (lValue instanceof TEMP)
            return lValue;
        return new MEM(lValue);
    }

    public IRGenerator(NameSpace symbolTable) {
        this.curClassName = null;
        this.curMethodName = null;
        this.curId = 0;
        this.symbolTable = symbolTable;
        this.fragments = new HashMap<Symbol, Stm>();
    }

    public Stm visit(Program p) {
        p.m.accept(this);
        for (ClassDecl cd : p.cl) {
            cd.accept(this);
        }
        return null;
    }

    public Stm visit(MainClass mc) {
        // save class name
        this.curClassName = Symbol.symbol(mc.nameOfMainClass.s);

        // Create label for function
        NameOfLabel fLabel = new NameOfLabel(this.curClassName.toString(), "main", "preludeEnd");

        // Create function body
        Stm body = mc.body.accept(this);

        // Create jump to epilogue
        NameOfLabel feLabel = new NameOfLabel(this.curClassName.toString(), "main", "epilogBegin");
        Stm jump = new JUMP(feLabel);

        // Put pieces together
        Stm main = new SEQ(new LABEL(fLabel), new SEQ(body, jump));

        // Add to list of functions
        this.fragments.put(Symbol.symbol(NameOfLabel.concat(this.curClassName.toString(), "main")), main);

        return null;
    }

    public Stm visit(SimpleClassDecl cd) {
        // save class name
        this.curClassName = Symbol.symbol(cd.i.s);
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        return null;
    }

    public Stm visit(ExtendingClassDecl cd) {
        // save class name
        this.curClassName = Symbol.symbol(cd.i.s);
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        return null;
    }

    public Stm visit(MethodDecl md) {
        // Create label for function
        this.curMethodName = Symbol.symbol(md.i.s);
        NameOfLabel fLabel = new NameOfLabel(this.curClassName.toString(), this.curMethodName.toString(), "preludeEnd");

        // Create function body
        Stm body;
        if (md.sl.size() > 0) {
            body = md.sl.get(md.sl.size()-1).accept(this);
            for (int i = md.sl.size()-2; i >= 0; i--)
                body = new SEQ(md.sl.get(i).accept(this), body);
        } else {
            body = new EVAL(new CONST(0));
        }

        // Move return value into register
        Stm ret = new MOVE(new TEMP("%i0"), this.toExp(md.e.accept(this)));

        // Create jump to epilogue
        NameOfLabel feLabel = new NameOfLabel(this.curClassName.toString(), this.curMethodName.toString(), "epilogBegin");
        Stm jump = new JUMP(feLabel);

        // Put pieces together
        Stm function = new SEQ(new LABEL(fLabel), new SEQ(new SEQ(body, ret), jump));

        // Add to list of functions
        fragments.put(Symbol.symbol(NameOfLabel.concat(this.curClassName.toString(), this.curMethodName.toString())), function);
        return null;
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
        Stm bodyStm = new SEQ(new LABEL(bodyLabel), new SEQ(body, new SEQ(new JUMP(loopLabel), new LABEL(endLabel))));

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
        Stm move = new MOVE(lvalue instanceof TEMP ? lvalue : new MEM(lvalue), rvalue);

        // Return
        return move;
    }

    public Stm visit(ArrayAssign aa) {
        // Determine memory address of array
        Exp array = this.toRValue(this.toExp(aa.nameOfArray.accept(this)));

        // Get the l-value using offset
        Exp lvalue = this.getArrayOffsetLValue(array, this.toExp(aa.indexInArray.accept(this)));

        // Determine expression for r-value
        Exp rvalue = this.toExp(aa.e.accept(this));

        // Move r-value to l-value
        Stm move = new MOVE(new MEM(lvalue), rvalue);

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
        NameOfLabel trueLabel = this.genLabelWithIndex("less", "than");
        NameOfLabel falseLabel = this.genLabelWithIndex("less", "than");
        NameOfLabel afterFalseLabel = this.genLabelWithIndex("less", "than");

        // Create true case
        Stm ifTrue = new SEQ(new SEQ(new LABEL(trueLabel), new MOVE(new TEMP(ltTemp), new CONST(1))), new JUMP(afterFalseLabel));

        // Create false case
        Stm ifFalse = new SEQ(new SEQ(new LABEL(falseLabel), new MOVE(new TEMP(ltTemp), new CONST(0))), new JUMP(afterFalseLabel));

        // Create a CJUMP
        Exp left = this.toExp(lt.e1.accept(this));
        Exp right = this.toExp(lt.e2.accept(this));
        Stm cjump = new CJUMP(CJUMP.LT, left, right, trueLabel, falseLabel);

        // Create expression for temp
        Exp e = new TEMP(ltTemp);

        // Return CEXP with statement and expression
        return this.toStm(new RET(new SEQ(cjump, new SEQ(ifTrue, new SEQ(ifFalse, new LABEL(afterFalseLabel)))), e));
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
        // Find label of function
        String className = this.getNameForMethod(c.getReceiverClassName(), c.i.s);
        NameOfLabel functionLabel = new NameOfLabel(className, c.i.s);
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
        return this.toStm(lvalue instanceof TEMP ? lvalue : new MEM(lvalue));
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
        return this.toStm(new RET(new SEQ(getSize, new SEQ(getAddr, initLength)), new TEMP(addrTemp)));
    }

    public Stm visit(NewObject no) {
        // Calculate size of object
        int objSize = ((ClassType) this.symbolTable.getType(Symbol.symbol(no.i.s))).getObjectSize(this.symbolTable);

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
