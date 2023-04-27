package AbstractSyntax;

import java.io.PrintWriter;
import syntax.*;

public class TreePrinter implements SyntaxTreeVisitor <Void> {
    /**
     * Class used to print an abstract syntax tree
     * @param pw - The PrintWriter to print the tree to
     * @param tab - The number of tabs the print out
     */
    private final PrintWriter pw;
    public TreePrinter(PrintWriter pw) {
        this.pw = pw;
    }
    public TreePrinter() {
        this.pw = new PrintWriter(System.out);
    }

    private int tab = 0;
    private void tab() {
        /**
         * Prints the tabs needed on a given line
         */
        for (int i = 0; i < tab; i++)
            this.print("    ");
    }

    private void print(String s) {
        /**
         * Prints a given string to the PrintWriter
         * @param s - The string to print out
         */
        this.pw.print(s);
    }
    private void println(String s) {
        /**
         * Prints a given string to the PrintWriter with a trailing newline
         * @param s - The string to print out
         */
        this.pw.println(s);
    }

    public Void visit(Program p) {
        tab = 0;
        if (p == null) {
            this.print("Null program!");
        } else if (p.m == null) {
            this.print("Null main class!");
        } else {
            p.m.accept(this);
            for (ClassDecl cd : p.cl) {
                cd.accept(this);
            }
            assert tab == 0;
        }
        this.pw.flush();
        return null;
    }

    public Void visit(MainClass mc) {
        tab();
        this.print("class ");
        mc.nameOfMainClass.accept(this);
        this.println(" {");
        tab++;

        tab();
        this.print("public static void main(String[] ");
        mc.nameOfCommandLineArgs.accept(this);
        this.println(") {");
        tab++;

        mc.body.accept(this);
        tab--;
        tab();
        this.println("}");
        tab--;
        tab();
        this.println("}");
        return null;
    }

    public Void visit(SimpleClassDecl cd) {
        tab();
        this.print("class ");
        cd.i.accept(this);
        this.println(" {");
        tab++;

        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        tab--;
        tab();
        this.println("}");
        return null;
    }

    public Void visit(ExtendingClassDecl cd) {
        tab();
        this.print("class ");
        cd.i.accept(this);
        this.print(" extends ");
        cd.j.accept(this);
        this.println(" {");
        tab++;

        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }

        tab--;
        tab();
        this.println("}");
        return null;
    }

    public Void visit(MethodDecl md) {
        tab();
        this.print("public ");
        md.t.accept(this);
        this.print(" ");
        md.i.accept(this);
        this.print(" (");
        if (md.formals.size() > 0)
            md.formals.get(0).accept(this);
        for (int i = 1; i < md.formals.size(); i++) {
            this.print(", ");
            md.formals.get(i).accept(this);
        }
        this.println(") {");
        tab++;

        for (LocalDecl ld : md.locals) {
            ld.accept(this);
        }
        for (Statement s : md.sl) {
            s.accept(this);
        }

        tab();
        this.print("return ");
        md.e.accept(this);
        this.println(";");
        tab--;
        tab();
        this.println("}");
        return null;
    }

    public Void visit(FieldDecl fd) {
        this.tab();
        fd.t.accept(this);
        this.print(" ");
        fd.i.accept(this);
        this.println(";");
        return null;
    }

    public Void visit(LocalDecl ld) {
        this.tab();
        ld.t.accept(this);
        this.print(" ");
        ld.i.accept(this);
        this.println(";");
        return null;
    }

    public Void visit(FormalDecl fd) {
        fd.t.accept(this);
        this.print(" ");
        fd.i.accept(this);
        return null;
    }

    public Void visit(IntArrayType iat) {
        this.print("int[]");
        return null;
    }

    public Void visit(BooleanType bt) {
        this.print("boolean");
        return null;
    }

    public Void visit(IntegerType it) {
        this.print("int");
        return null;
    }

    public Void visit(VoidType vt) {
        this.print("void");
        return null;
    }

    public Void visit(IdentifierType it) {
        this.print(it.nameOfType);
        return null;
    }

    public Void visit(final Block b) {
        tab();
        this.println("{");
        tab++;

        for (Statement s : b.sl) {
            s.accept(this);
        }

        tab--;
        tab();
        this.println("}");
        return null;
    }

    public Void visit(final If f) {
        tab();
        this.print("if (");
        f.e.accept(this);
        this.print(")");

        if (f.s1 instanceof Block) {
            this.println(" {");
            tab++;
            for (Statement s : ((Block) f.s1).sl) {
                s.accept(this);
            }
            tab--;
            tab();
            this.print("} ");
        } else {
            this.println("");
            tab++;
            f.s1.accept(this);
            tab--;
        }

        tab();
        this.print("else");
        if (f.s2 instanceof Block) {
            this.println(" {");
            tab++;
            for (Statement s : ((Block) f.s2).sl) {
                s.accept(this);
            }
            tab--;
            tab();
            this.println("}");
        } else {
            this.println("");
            tab++;
            f.s2.accept(this);
            tab--;
        }
        return null;
    }

    public Void visit(final While w) {
        tab();
        this.print("while (");
        w.e.accept(this);
        this.print(")");

        if (w.s instanceof Block) {
            this.println(" {");
            tab++;
            for (Statement s : ((Block) w.s).sl) {
                s.accept(this);
            }
            tab--;
            tab();
            this.println("}");
        } else {
            this.println("");
            tab++;
            w.s.accept(this);
            tab--;
        }
        return null;
    }

    public Void visit(final Print p) {
        tab();
        this.print("System.out.println(");
        p.e.accept(this);
        this.println(");");
        return null;
    }

    public Void visit(final Assign a) {
        this.tab();
        a.i.accept(this);
        this.print(" = ");
        a.e.accept(this);
        this.println(";");
        return null;
    }

    public Void visit(ArrayAssign aa) {
        this.tab();
        aa.nameOfArray.accept(this);
        this.print("[");
        aa.indexInArray.accept(this);
        this.print("] = ");
        aa.e.accept(this);
        this.println(";");
        return null;
    }

    public Void visit(final And a) {
        this.print("(");
        a.e1.accept(this);
        this.print(" && ");
        a.e2.accept(this);
        this.print(")");
        return null;
    }

    public Void visit(final LessThan lt) {
        this.print("(");
        lt.e1.accept(this);
        this.print(" < ");
        lt.e2.accept(this);
        this.print(")");
        return null;
    }

    public Void visit(final Plus p) {
        this.print("(");
        p.e1.accept(this);
        this.print(" + ");
        p.e2.accept(this);
        this.print(")");
        return null;
    }

    public Void visit(final Minus m) {
        this.print("(");
        m.e1.accept(this);
        this.print(" - ");
        m.e2.accept(this);
        this.print(")");
        return null;
    }

    public Void visit(final Times t) {
        this.print("(");
        t.e1.accept(this);
        this.print(" * ");
        t.e2.accept(this);
        this.print(")");
        return null;
    }

    public Void visit(final ArrayLookup al) {
        al.expressionForArray.accept(this);
        this.print("[");
        al.indexInArray.accept(this);
        this.print("]");
        return null;
    }

    public Void visit(final ArrayLength al) {
        al.expressionForArray.accept(this);
        this.print(".length");
        return null;
    }

    public Void visit(Call c) {
        c.e.accept(this);
        this.print(".");
        c.i.accept(this);
        this.print("(");

        if (c.el.size() > 0)
            c.el.get(0).accept(this);
        for (int i = 1; i < c.el.size(); i++) {
            this.print(", ");
            c.el.get(i).accept(this);
        }

        this.print(")");
        return null;
    }

    public Void visit(True t) {
        this.print("true");
        return null;
    }

    public Void visit(False f) {
        this.print("false");
        return null;
    }

    public Void visit(IntegerLiteral il) {
        this.print(String.valueOf(il.i));
        return null;
    }

    public Void visit(IdentifierExp ie) {
        this.print(ie.s);
        return null;
    }

    public Void visit(This t) {
        this.print("this");
        return null;
    }

    public Void visit(NewArray na) {
        this.print("new int[");
        na.e.accept(this);
        this.print("]");
        return null;
    }

    public Void visit(NewObject no) {
        this.print("new ");
        no.i.accept(this);
        this.print("()");
        return null;
    }

    public Void visit(Not n) {
        this.print("!");
        n.e.accept(this);
        return null;
    }

    public Void visit(Identifier id) {
        print(id.s);
        return null;
    }

}
