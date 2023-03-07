package SemanticChecking;

import SemanticChecking.Symbol.BasicType;
import SemanticChecking.Symbol.ClassType;
import SemanticChecking.Symbol.MethodType;
import SemanticChecking.Symbol.Symbol;

import java.io.PrintWriter;
import java.util.HashMap;

public class SymbolTableFactory extends SyntaxTreeVisitor <Void> {
    private final PrintWriter pw;
    HashMap<Symbol, ClassType> classes;
    ClassType curClass;
    MethodType curMethod;

    private void debug(String s) {
        if (this.pw != null) {
            pw.println(s);
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
            return ((IdentifierType) t).s;
        }
        return "ERROR";
    }

    public SymbolTableFactory() {
        this.classes = new HashMap<Symbol, ClassType>();
        this.curClass = null;
        this.curMethod = null;
        this.pw = null;
    }

    public SymbolTableFactory(PrintWriter pw) {
        this.classes = new HashMap<Symbol, ClassType>();
        this.curClass = null;
        this.curMethod = null;
        this.pw = pw;
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
        this.debug("Exploring class " + mc.i1.s);
        this.curClass = new ClassType(mc.i1.s);

        // Exploring main method
        this.debug("Exploring method " + mc.i1.s + ".main");
        this.curMethod = new MethodType("main");
        if (!this.checkMethodVar(new Symbol(mc.i2.s))) {
            // TODO Error
        } else {
            this.debug(mc.i1.s + ".main: " + mc.i2.s + "::String[]");
            this.curMethod.setArg(new Symbol(mc.i2.s), new BasicType("String[]"));
        }
        mc.s.accept(this);

        // Adding main method
        if (!this.checkClassVar(new Symbol("main"))) {
            // TODO Error
        } else {
            this.curClass.setVar(new Symbol("main"), this.curMethod);
        }
        this.curMethod = null;

        if (!this.checkClassName(new Symbol(mc.i1.s))) {
            // TODO error
        } else {
            this.classes.put(new Symbol(mc.i1.s), this.curClass);
        }
        this.curClass = null;
        return null;
    }

    public Void visit(SimpleClassDecl cd) {
        this.debug("Exploring class " + cd.i.s);
        this.curClass = new ClassType(cd.i.s);

        for (FieldDecl fd : cd.vl) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.ml) {
            md.accept(this);
        }

        if (!this.checkClassName(new Symbol(cd.i.s))) {
            // TODO error
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
        for (FieldDecl fd : cd.vl) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.ml) {
            md.accept(this);
        }

        if (!this.checkClassName(new Symbol(cd.i.s))) {
            // TODO error
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
        for (FormalDecl f : md.fl) {
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
            // TODO error
        } else {
            this.curClass.setVar(new Symbol(md.i.s), this.curMethod);
        }
        this.curMethod = null;
        return null;
    }

    public Void visit(FieldDecl fd) {
        if (!this.checkClassVar(fd.i.s)) {
            // TODO error
        } else {
            String typeStr = this.getTypeString(fd.t);
            this.curClass.setVar(new Symbol(fd.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(LocalDecl ld) {
        if (!this.checkMethodVar(ld.i.s)) {
            // TODO error
        } else {
            String typeStr = this.getTypeString(ld.t);
            this.curMethod.setVar(new Symbol(ld.i.s), new BasicType(typeStr));
        }
        return null;
    }

    public Void visit(FormalDecl fd) {
        if (!this.checkMethodVar(fd.i.s)) {
            // TODO error
        } else {
            String typeStr = this.getTypeString(fd.t);
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
        for (Statement s : b.sl) {
            s.accept(this);
        }
        return null;
    }

    public Void visit(final If f) {
        f.e.accept(this);
        f.s1.accept(this);
        f.s2.accept(this);
        return null;
    }

    public Void visit(final While w) {
        w.e.accept(this);
        w.s.accept(this);
        return null;
    }

    public Void visit(final Print p) {
        p.e.accept(this);
        return null;
    }

    public Void visit(final Assign a) {
        a.i.accept(this);
        a.e.accept(this);
        return null;
    }

    public Void visit(ArrayAssign aa) {
        aa.nameOfArray.accept(this);
        aa.indexInArray.accept(this);
        aa.e.accept(this);
        return null;
    }

    public Void visit(final And a) {
        a.e1.accept(this);
        a.e2.accept(this);
        return null;
    }

    public Void visit(final LessThan lt) {
        lt.e1.accept(this);
        lt.e2.accept(this);
        return null;
    }

    public Void visit(final Plus p) {
        p.e1.accept(this);
        p.e2.accept(this);
        return null;
    }

    public Void visit(final Minus m) {
        m.e1.accept(this);
        m.e2.accept(this);
        return null;
    }

    public Void visit(final Times t) {
        t.e1.accept(this);
        t.e2.accept(this);
        return null;
    }

    public Void visit(final ArrayLookup al) {
        al.expressionForArray.accept(this);
        al.indexInArray.accept(this);
        return null;
    }

    public Void visit(final ArrayLength al) {
        al.expressionForArray.accept(this);
        return null;
    }

    public Void visit(Call c) {
        c.e.accept(this);
        for (Expression e : c.el) {
            e.accept(this);
        }
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
        if (this.checkMethodVar(new Symbol(ie.s))) {
            // TODO Error
        }
        return null;
    }

    public Void visit(This t) {
        return null;
    }

    public Void visit(NewArray na) {
        na.e.accept(this);
        return null;
    }

    public Void visit(NewObject no) {
        return null;
    }

    public Void visit(Not n) {
        n.e.accept(this);
        return null;
    }

    public Void visit(Identifier id) {
        return null;
    }

}
