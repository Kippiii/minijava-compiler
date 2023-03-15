package SemanticChecking;

import ErrorManagement.CompilerException;
import SemanticChecking.Symbol.BasicType;
import SemanticChecking.Symbol.ClassType;
import SemanticChecking.Symbol.MethodType;
import SemanticChecking.Symbol.Symbol;
import syntax.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableFactory implements SyntaxTreeVisitor <Void> {
    private final boolean debug;
    HashMap<Symbol, ClassType> classes;
    ClassType curClass;
    MethodType curMethod;
    List<CompilerException> errors;

    private void debug(String s) {
        if (this.debug) {
            System.out.println(s);
        }
    }

    private boolean checkMethodVar(Symbol s) {
        SemanticChecking.Symbol.Type arg = this.curMethod.getArgType(s);
        if (arg != null)
            return false;
        return this.curMethod.getVarType(s) == null;
    }

    private boolean checkClassVar(Symbol s) {
        return this.curClass.getVarType(s) == null;
    }

    private boolean checkClassName(Symbol s) {
        ClassType ct = this.classes.get(s);
        return ct == null;
    }

    private String getTypeString(Type t) {
        if (t instanceof IntArrayType) {
            return "int[]";
        }
        if (t instanceof BooleanType) {
            return "boolean";
        }
        if (t instanceof IntegerType) {
            return "int";
        }
        if (t instanceof VoidType) {
            return "void";
        }
        if (t instanceof IdentifierType) {
            return ((IdentifierType) t).nameOfType;
        }
        return "ERROR";
    }

    public SymbolTableFactory() {
        this.classes = new HashMap<Symbol, ClassType>();
        this.curClass = null;
        this.curMethod = null;
        this.debug = false;
        this.errors = new ArrayList<CompilerException>();
    }

    public SymbolTableFactory(boolean debug) {
        this.classes = new HashMap<Symbol, ClassType>();
        this.curClass = null;
        this.curMethod = null;
        this.debug = debug;
        this.errors = new ArrayList<CompilerException>();
    }

    public Void visit(Program p) {
        if (p == null || p.m == null) {
            return null;
        }
        p.m.accept(this);
        for (ClassDecl cd : p.cl) {
            cd.accept(this);
        }
        return null;
    }

    public Void visit(MainClass mc) {
        this.debug("Exploring class " + mc.nameOfMainClass.s);
        this.curClass = new ClassType(mc.nameOfMainClass.s);

        // Exploring main method
        this.debug("Exploring method " + mc.nameOfMainClass.s + ".main");
        this.curMethod = new MethodType("main");
        if (!this.checkMethodVar(new Symbol(mc.nameOfCommandLineArgs.s))) {
            this.errors.add(new NameConflictError(mc.nameOfCommandLineArgs.s, mc.nameOfCommandLineArgs.lineNumber, mc.nameOfCommandLineArgs.columnNumber));
        } else {
            this.debug(mc.nameOfMainClass.s + ".main: " + mc.nameOfCommandLineArgs.s + "::String[]");
            this.curMethod.setArg(new Symbol(mc.nameOfCommandLineArgs.s), new BasicType("String[]"));
        }
        mc.body.accept(this);

        // Adding main method
        if (!this.checkClassVar(new Symbol("main"))) {
            this.errors.add(new NameConflictError("main", mc.lineNumber, mc.columnNumber));
        } else {
            this.curClass.setVar(new Symbol("main"), this.curMethod);
        }
        this.curMethod = null;

        if (!this.checkClassName(new Symbol(mc.nameOfMainClass.s))) {
            this.errors.add(new NameConflictError(mc.nameOfMainClass.s, mc.nameOfMainClass.lineNumber, mc.nameOfMainClass.columnNumber));
        } else {
            this.classes.put(new Symbol(mc.nameOfMainClass.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(SimpleClassDecl cd) {
        this.debug("Exploring class " + cd.i.s);
        this.curClass = new ClassType(cd.i.s);

        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        if (!this.checkClassName(new Symbol(cd.i.s))) {
            this.errors.add(new NameConflictError(cd.i.s, cd.i.lineNumber, cd.i.columnNumber));
        } else {
            this.classes.put(new Symbol(cd.i.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(ExtendingClassDecl cd) {
        this.debug("Exploring class " + cd.i.s);
        this.curClass = new ClassType(cd.i.s);

        this.curClass.setExtName(new Symbol(cd.j.s));
        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        if (!this.checkClassName(new Symbol(cd.i.s))) {
            this.errors.add(new NameConflictError(cd.i.s, cd.i.lineNumber, cd.i.columnNumber));
        } else {
            this.classes.put(new Symbol(cd.i.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(MethodDecl md) {
        this.debug("Exploring method " + this.curClass.getName() + "." + md.i.s);
        this.curMethod = new MethodType(md.i.s);

        String retTypeStr = this.getTypeString(md.t);
        this.curMethod.setRetType(new BasicType(retTypeStr));
        for (FormalDecl f : md.formals) {
            f.accept(this);
        }
        for (LocalDecl ld : md.locals) {
            ld.accept(this);
        }
        for (Statement s : md.sl) {
            s.accept(this);
        }
        md.e.accept(this);

        if (!this.checkClassVar(new Symbol(md.i.s))) {
            this.errors.add(new NameConflictError(md.i.s, md.i.lineNumber, md.i.columnNumber));
        } else {
            this.curClass.setVar(new Symbol(md.i.s), this.curMethod);
        }
        this.curMethod = null;
        return null;
    }

    public Void visit(FieldDecl fd) {
        if (!this.checkClassVar(new Symbol(fd.i.s))) {
            this.errors.add(new NameConflictError(fd.i.s, fd.i.lineNumber, fd.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(fd.t);
            debug(this.curClass.getName() + "." + this.curMethod.getName() + ": " + fd.i.s + "::" + typeStr);
            this.curClass.setVar(new Symbol(fd.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(LocalDecl ld) {
        if (!this.checkMethodVar(new Symbol(ld.i.s))) {
            this.errors.add(new NameConflictError(ld.i.s, ld.i.lineNumber, ld.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(ld.t);
            debug(this.curClass.getName() + "." + this.curMethod.getName() + ": " + ld.i.s + "::" + typeStr);
            this.curMethod.setVar(new Symbol(ld.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(FormalDecl fd) {
        if (!this.checkMethodVar(new Symbol(fd.i.s))) {
            this.errors.add(new NameConflictError(fd.i.s, fd.i.lineNumber, fd.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(fd.t);
            debug(this.curClass.getName() + "." + this.curMethod.getName() + ": " + fd.i.s + "::" + typeStr);
            this.curMethod.setArg(new Symbol(fd.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(IntArrayType iat) {
        return null;
    }

    public Void visit(BooleanType bt) {
        return null;
    }

    public Void visit(IntegerType it) {
        return null;
    }

    public Void visit(VoidType vt) {
        return null;
    }

    public Void visit(IdentifierType it) {
        return null;
    }

    public Void visit(final Block b) {
        return null;
    }

    public Void visit(final If f) {
        return null;
    }

    public Void visit(final While w) {
        return null;
    }

    public Void visit(final Print p) {
        return null;
    }

    public Void visit(final Assign a) {
        a.i.accept(this);
        return null;
    }

    public Void visit(ArrayAssign aa) {
        aa.nameOfArray.accept(this);
        return null;
    }

    public Void visit(final And a) {
        return null;
    }

    public Void visit(final LessThan lt) {
        return null;
    }

    public Void visit(final Plus p) {
        return null;
    }

    public Void visit(final Minus m) {
        return null;
    }

    public Void visit(final Times t) {
        return null;
    }

    public Void visit(final ArrayLookup al) {
        return null;
    }

    public Void visit(final ArrayLength al) {
        return null;
    }

    public Void visit(Call c) {
        return null;
    }

    public Void visit(True t) {
        return null;
    }

    public Void visit(False f) {
        return null;
    }

    public Void visit(IntegerLiteral il) {
        return null;
    }

    public Void visit(IdentifierExp ie) {
        return null;
    }

    public Void visit(This t) {
        return null;
    }

    public Void visit(NewArray na) {
        return null;
    }

    public Void visit(NewObject no) {
        return null;
    }

    public Void visit(Not n) {
        return null;
    }

    public Void visit(Identifier id) {
        return null;
    }

}
