package IRTranslation;

import syntax.*;
import tree.*;

public class IRGenerator implements SyntaxTreeVisitor<Stm> {

    public Stm visit(Program p) {
        p.m.accept(this);
        for (ClassDecl cd : p.cl) {
            cd.accept(this);
        }
        return null;
    }

    public Stm visit(MainClass mc) {
        // TODO
    }

    public Stm visit(SimpleClassDecl cd) {
        // TODO
    }

    public Stm visit(ExtendingClassDecl cd) {
        // TODO
    }

    public Stm visit(MethodDecl md) {
        // TODO
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
        // TODO
    }

    public Stm visit(BooleanType bt) {
        // TODO
    }

    public Stm visit(IntegerType it) {
        // TODO
    }

    public Stm visit(VoidType vt) {
        // TODO
    }

    public Stm visit(IdentifierType it) {
        // TODO
    }

    public Stm visit(final Block b) {
        // TODO
    }

    public Stm visit(final If f) {
        // TODO
    }

    public Stm visit(final While w) {
        // TODO
    }

    public Stm visit(final Print p) {
        // TODO
    }

    public Stm visit(final Assign a) {
        // TODO
    }

    public Stm visit(ArrayAssign aa) {
        // TODO
    }

    public Stm visit(final And a) {
        // TODO
    }

    public Stm visit(final LessThan lt) {
        // TODO
    }

    public Stm visit(final Plus p) {
        // TODO
    }

    public Stm visit(final Minus m) {
        // TODO
    }

    public Stm visit(final Times t) {
        // TODO
    }

    public Stm visit(final ArrayLookup al) {
        // TODO
    }

    public Stm visit(final ArrayLength al) {
        // TODO
    }

    public Stm visit(Call c) {
        // TODO
    }

    public Stm visit(True t) {
        // TODO
    }

    public Stm visit(False f) {
        // TODO
    }

    public Stm visit(IntegerLiteral il) {
        // TODO
    }

    public Stm visit(IdentifierExp ie) {
        // TODO
    }

    public Stm visit(This t) {
        // TODO
    }

    public Stm visit(NewArray na) {
        // TODO
    }

    public Stm visit(NewObject no) {
        // TODO
    }

    public Stm visit(Not n) {
        // TODO
    }

    public Stm visit(Identifier id) {
        // TODO
    }

}
