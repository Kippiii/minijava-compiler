package SemanticChecking;

import ErrorManagement.CompilerException;
import SemanticChecking.Symbol.*;
import syntax.*;
import syntax.Type;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableFactory implements SyntaxTreeVisitor <Void> {
    private final boolean debug;
    NameSpace classes;
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
        return this.curMethod.getLocalType(s) == null;
    }

    private boolean checkClassField(Symbol s) {
        return this.curClass.getFieldType(s) == null;
    }

    private boolean checkClassMethod(Symbol s) {
        return this.curClass.getMethodType(s) == null;
    }

    private boolean checkClassName(Symbol s) {
        ClassType ct = (ClassType) this.classes.getType(s);
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
        this.classes = new NameSpace();
        this.curClass = null;
        this.curMethod = null;
        this.debug = false;
        this.errors = new ArrayList<CompilerException>();
    }

    public SymbolTableFactory(boolean debug) {
        this.classes = new NameSpace();
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
        this.curClass = new ClassType(mc.nameOfMainClass.s, mc.nameOfMainClass.lineNumber, mc.nameOfMainClass.columnNumber);

        // Exploring main method
        this.debug("Exploring method " + mc.nameOfMainClass.s + ".main");
        this.curMethod = new MethodType("main");
        this.curMethod.setRetType(new BasicType("void"));
        if (!this.checkMethodVar(Symbol.symbol(mc.nameOfCommandLineArgs.s))) {
            this.errors.add(new NameConflictError(mc.nameOfCommandLineArgs.s, mc.nameOfCommandLineArgs.lineNumber, mc.nameOfCommandLineArgs.columnNumber));
        } else {
            this.debug(mc.nameOfMainClass.s + ".main: " + mc.nameOfCommandLineArgs.s + "::String[]");
            this.curMethod.setArg(Symbol.symbol(mc.nameOfCommandLineArgs.s), new BasicType("String[]"));
        }
        this.curMethod.setMain();
        mc.body.accept(this);

        // Adding main method
        if (!this.checkClassMethod(Symbol.symbol("main"))) {
            this.errors.add(new NameConflictError("main", mc.lineNumber, mc.columnNumber));
        } else {
            this.curClass.setMethod(Symbol.symbol("main"), this.curMethod);
        }
        this.curMethod = null;

        if (!this.checkClassName(Symbol.symbol(mc.nameOfMainClass.s))) {
            this.errors.add(new NameConflictError(mc.nameOfMainClass.s, mc.nameOfMainClass.lineNumber, mc.nameOfMainClass.columnNumber));
        } else {
            this.classes.add(Symbol.symbol(mc.nameOfMainClass.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(SimpleClassDecl cd) {
        this.debug("Exploring class " + cd.i.s);
        this.curClass = new ClassType(cd.i.s, cd.i.lineNumber, cd.i.columnNumber);

        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        if (!this.checkClassName(Symbol.symbol(cd.i.s))) {
            this.errors.add(new NameConflictError(cd.i.s, cd.i.lineNumber, cd.i.columnNumber));
        } else {
            this.classes.add(Symbol.symbol(cd.i.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(ExtendingClassDecl cd) {
        this.debug("Exploring class " + cd.i.s);
        this.curClass = new ClassType(cd.i.s, cd.i.lineNumber, cd.i.columnNumber);

        this.curClass.setExtName(Symbol.symbol(cd.j.s));
        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        if (!this.checkClassName(Symbol.symbol(cd.i.s))) {
            this.errors.add(new NameConflictError(cd.i.s, cd.i.lineNumber, cd.i.columnNumber));
        } else {
            this.classes.add(Symbol.symbol(cd.i.s), this.curClass);
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

        if (!this.checkClassMethod(Symbol.symbol(md.i.s))) {
            this.errors.add(new NameConflictError(md.i.s, md.i.lineNumber, md.i.columnNumber));
        } else {
            this.curClass.setMethod(Symbol.symbol(md.i.s), this.curMethod);
        }
        this.curMethod = null;
        return null;
    }

    public Void visit(FieldDecl fd) {
        if (!this.checkClassField(Symbol.symbol(fd.i.s))) {
            this.errors.add(new NameConflictError(fd.i.s, fd.i.lineNumber, fd.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(fd.t);
            debug(this.curClass.getName() + ": " + fd.i.s + "::" + typeStr);
            this.curClass.setField(Symbol.symbol(fd.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(LocalDecl ld) {
        if (!this.checkMethodVar(Symbol.symbol(ld.i.s))) {
            this.errors.add(new NameConflictError(ld.i.s, ld.i.lineNumber, ld.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(ld.t);
            debug(this.curClass.getName() + "." + this.curMethod.getName() + ": " + ld.i.s + "::" + typeStr);
            this.curMethod.setLocal(Symbol.symbol(ld.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(FormalDecl fd) {
        if (!this.checkMethodVar(Symbol.symbol(fd.i.s))) {
            this.errors.add(new NameConflictError(fd.i.s, fd.i.lineNumber, fd.i.columnNumber));
        } else {
            String typeStr = this.getTypeString(fd.t);
            debug(this.curClass.getName() + "." + this.curMethod.getName() + ": " + fd.i.s + "::" + typeStr);
            this.curMethod.setArg(Symbol.symbol(fd.i.s), new BasicType(typeStr));
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
