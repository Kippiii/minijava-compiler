package SemanticChecking;

public class SymbolTableFactory extends SyntaxTreeVisitor <Void> {
    Table<Void> table;

    public DeclarationChecker() {
        this.table = new Table<Void>();
    }

    public Void visit(Program p) {

    }

    public Void visit(MainClass mc) {

    }

    public Void visit(SimpleClassDecl cd) {

    }

    public Void visit(ExtendingClassDecl cd) {

    }

    public Void visit(MethodDecl md) {

    }

    public Void visit(fieldDecl fd) {

    }

    public Void visit(LocalDecl ld) {

    }

    public Void visit(FormalDecl fd) {

    }

    public Void visit(IntArrayType iat) {

    }

    public Void visit(BooleanType bt) {

    }

    public Void visit(IntegerType it) {

    }

    public Void visit(VoidType vt) {

    }

    public Void visit(IdentifierType it) {

    }

    public Void visit(final Block b) {

    }

    public Void visit(final If f) {

    }

    public Void visit(final While w) {

    }

    public Void visit(final Print p) {

    }

    public Void visit(final Assign a) {

    }

    public Void visit(ArrayAssign aa) {

    }

    public Void visit(final And a) {

    }

    public Void visit(final LessThan lt) {

    }

    public Void visit(final Plus p) {

    }

    public Void visit(final Minus m) {

    }

    public Void visit(final Times t) {

    }

    public Void visit(final ArrayLookup al) {

    }

    public Void visit(final ArrayLength al) {

    }

    public Void visit(Call c) {

    }

    public Void visit(True t) {

    }

    public Void visit(False f) {

    }

    public Void visit(IntegerLiteral il) {

    }

    public Void visit(IdentifierExp ie) {

    }

    public Void visit(This t) {

    }

    public Void visit(NewArray na) {

    }

    public Void visit(NewObject no) {

    }

    public Void visit(Not n) {

    }

    public Void visit(Identifier id) {

    }

}
