package SemanticChecking;

import ErrorManagement.CompilerException;
import SemanticChecking.Symbol.*;
import syntax.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeChecker implements SyntaxTreeVisitor<SemanticChecking.Symbol.Type> {
    HashMap<Symbol, ClassType> symbolTable;
    ClassType curClass;
    MethodType curMethod;
    List<CompilerException> errors;

    SemanticChecking.Symbol.Type getTypeOfSymbol(Symbol s) {
        if (this.curMethod != null) {
            SemanticChecking.Symbol.Type t = this.curMethod.getType(s);
            if (t != null) {
                return t;
            }
        }
        ClassType ct = this.curClass;
        while (ct != null) {
            SemanticChecking.Symbol.Type t = ct.getVarType(s);
            if (t != null) {
                return t;
            }

            if (ct.getExtName() != null) {
                ct = symbolTable.get(ct.getExtName());
            } else {
                ct = null;
            }
        }
        return this.symbolTable.get(s);
    }

    boolean checkTypeEquals(SemanticChecking.Symbol.Type expected, SemanticChecking.Symbol.Type received) {
        if (expected == null || received == null) {
            return false;
        }
        if (expected.toString().equals(received.toString())) {
            return true;
        }
        ClassType ct = this.symbolTable.get(Symbol.symbol(received.toString()));
        if (ct != null) {
            if (ct.getExtName() == null) {
                return false;
            }
            return checkTypeEquals(expected, new BasicType(ct.getExtName().toString()));
        }
        return false;
    }

    boolean isInherited(Symbol field, Symbol parentName) {
        if (field == null || parentName == null) {
            return false;
        }
        ClassType ct = this.symbolTable.get(parentName);
        if (ct.getVarType(field) != null) {
            return true;
        }
        return isInherited(field, ct.getExtName());
    }

    ClassType getClassOfObj(SemanticChecking.Symbol.Type t) {
        return this.symbolTable.get(Symbol.symbol(t.toString()));
    }

    public TypeChecker(HashMap<Symbol, ClassType> symbolTable) {
        this.symbolTable = symbolTable;
        this.curClass = null;
        this.curMethod = null;
        this.errors = new ArrayList<CompilerException>();
    }

    public SemanticChecking.Symbol.Type visit(Program p) {
        if (p != null && p.m != null) {
            p.m.accept(this);
            for (ClassDecl cd : p.cl) {
                cd.accept(this);
            }
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(MainClass mc) {
        this.curClass = this.symbolTable.get(Symbol.symbol(mc.nameOfMainClass.s));
        mc.body.accept(this);
        this.curClass = null;
        return null;
    }

    public SemanticChecking.Symbol.Type visit(SimpleClassDecl cd) {
        this.curClass = this.symbolTable.get(Symbol.symbol(cd.i.s));
        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        this.curClass = null;
        return null;
    }

    public SemanticChecking.Symbol.Type visit(ExtendingClassDecl cd) {
        // Check extension type
        SemanticChecking.Symbol.Type extType = this.symbolTable.get(Symbol.symbol(cd.j.s));
        if (extType == null) {
            this.errors.add(new InvalidClassError(cd.j.s, cd.j.lineNumber, cd.j.columnNumber));
        }

        this.curClass = this.symbolTable.get(Symbol.symbol(cd.i.s));
        for (FieldDecl fd : cd.fields) {
            fd.accept(this);
        }
        for (MethodDecl md : cd.methods) {
            md.accept(this);
        }
        this.curClass = null;
        return null;
    }

    public SemanticChecking.Symbol.Type visit(MethodDecl md) {
        if (isInherited(Symbol.symbol(md.i.s), this.curClass.getExtName())) {
            this.errors.add(new NameConflictError(md.i.s, md.i.lineNumber, md.i.columnNumber));
        }

        this.curMethod = (MethodType) this.curClass.getVarType(Symbol.symbol(md.i.s));
        for (FormalDecl fd : md.formals) {
            fd.accept(this);
        }
        for (LocalDecl ld : md.locals) {
            ld.accept(this);
        }
        for (Statement s : md.sl) {
            s.accept(this);
        }
        SemanticChecking.Symbol.Type retType = md.e.accept(this);
        if (retType == null) {
            return null;
        }
        if (!checkTypeEquals(this.curMethod.getRetType(), retType)) {
            this.errors.add(new TypeMismatchError(retType.toString(), this.curMethod.getRetType().toString(), md.e.lineNumber, md.e.columnNumber));
        }
        this.curMethod = null;
        return null;
    }

    public SemanticChecking.Symbol.Type visit(FieldDecl fd) {
        fd.t.accept(this);
        if (isInherited(Symbol.symbol(fd.i.s), this.curClass.getExtName())) {
            this.errors.add(new NameConflictError(fd.i.s, fd.i.lineNumber, fd.i.columnNumber));
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(LocalDecl ld) {
        ld.t.accept(this);
        return null;
    }

    public SemanticChecking.Symbol.Type visit(FormalDecl fd) {
        fd.t.accept(this);
        return null;
    }

    public SemanticChecking.Symbol.Type visit(IntArrayType iat) {
        return null;
    }

    public SemanticChecking.Symbol.Type visit(BooleanType bt) {
        return null;
    }

    public SemanticChecking.Symbol.Type visit(IntegerType it) {
        return null;
    }

    public SemanticChecking.Symbol.Type visit(VoidType vt) {
        return null;
    }

    public SemanticChecking.Symbol.Type visit(IdentifierType it) {
        if (this.symbolTable.get(Symbol.symbol(it.nameOfType)) == null) {
            this.errors.add(new InvalidClassError(it.nameOfType, it.lineNumber, it.columnNumber));
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final Block b) {
        for (Statement s : b.sl) {
            s.accept(this);
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final If f) {
        SemanticChecking.Symbol.Type checkType = f.e.accept(this);
        if (checkType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("boolean"), checkType)) {
            this.errors.add(new TypeMismatchError("boolean", checkType.toString(), f.e.lineNumber, f.e.columnNumber));
        }
        f.s1.accept(this);
        f.s2.accept(this);
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final While w) {
        SemanticChecking.Symbol.Type checkType = w.e.accept(this);
        if (checkType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("boolean"), checkType)) {
            this.errors.add(new TypeMismatchError("boolean", checkType.toString(), w.e.lineNumber, w.e.columnNumber));
        }
        w.s.accept(this);
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final Print p) {
        // TODO Only ints printable?
        SemanticChecking.Symbol.Type printType = p.e.accept(this);
        if (printType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), printType)) {
            this.errors.add(new TypeMismatchError("int", printType.toString(), p.e.lineNumber, p.e.columnNumber));
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final Assign a) {
        SemanticChecking.Symbol.Type t1 = this.getTypeOfSymbol(Symbol.symbol(a.i.s));
        SemanticChecking.Symbol.Type t2 = a.e.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(t1, t2)) {
            this.errors.add(new TypeMismatchError(t1.toString(), t2.toString(), a.e.lineNumber, a.e.columnNumber));
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(ArrayAssign aa) {
        // Check that name is array
        SemanticChecking.Symbol.Type arrType = this.getTypeOfSymbol(Symbol.symbol(aa.nameOfArray.s));
        if (arrType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int[]"), arrType)) {
            this.errors.add(new TypeMismatchError("int[]", arrType.toString(), aa.nameOfArray.lineNumber, aa.nameOfArray.columnNumber));
        }

        // Check that index is an int
        SemanticChecking.Symbol.Type indexType = aa.indexInArray.accept(this);
        if (indexType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), indexType)) {
            this.errors.add(new TypeMismatchError("int", indexType.toString(), aa.indexInArray.lineNumber, aa.indexInArray.columnNumber));
        }

        // Check that expression is int
        SemanticChecking.Symbol.Type valueType = aa.e.accept(this);
        if (valueType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), valueType)) {
            this.errors.add(new TypeMismatchError("int", valueType.toString(), aa.e.lineNumber, aa.e.columnNumber));
        }
        return null;
    }

    public SemanticChecking.Symbol.Type visit(final And a) {
        SemanticChecking.Symbol.Type t1 = a.e1.accept(this);
        SemanticChecking.Symbol.Type t2 = a.e2.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("boolean"), t1)) {
            this.errors.add(new TypeMismatchError("boolean", t1.toString(), a.e1.lineNumber, a.e1.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("boolean"), t2)) {
            this.errors.add(new TypeMismatchError("boolean", t2.toString(), a.e2.lineNumber, a.e2.columnNumber));
        }
        return new BasicType("boolean");
    }

    public SemanticChecking.Symbol.Type visit(final LessThan lt) {
        SemanticChecking.Symbol.Type t1 = lt.e1.accept(this);
        SemanticChecking.Symbol.Type t2 = lt.e2.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), t1)) {
            this.errors.add(new TypeMismatchError("int", t1.toString(), lt.e1.lineNumber, lt.e1.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("int"), t2)) {
            this.errors.add(new TypeMismatchError("int", t2.toString(), lt.e2.lineNumber, lt.e2.columnNumber));
        }
        return new BasicType("boolean");
    }

    public SemanticChecking.Symbol.Type visit(final Plus p) {
        SemanticChecking.Symbol.Type t1 = p.e1.accept(this);
        SemanticChecking.Symbol.Type t2 = p.e2.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), t1)) {
            this.errors.add(new TypeMismatchError("int", t1.toString(), p.e1.lineNumber, p.e1.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("int"), t2)) {
            this.errors.add(new TypeMismatchError("int", t2.toString(), p.e2.lineNumber, p.e2.columnNumber));
        }
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(final Minus m) {
        SemanticChecking.Symbol.Type t1 = m.e1.accept(this);
        SemanticChecking.Symbol.Type t2 = m.e2.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), t1)) {
            this.errors.add(new TypeMismatchError("int", t1.toString(), m.e1.lineNumber, m.e1.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("int"), t2)) {
            this.errors.add(new TypeMismatchError("int", t2.toString(), m.e2.lineNumber, m.e2.columnNumber));
        }
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(final Times t) {
        SemanticChecking.Symbol.Type t1 = t.e1.accept(this);
        SemanticChecking.Symbol.Type t2 = t.e2.accept(this);
        if (t1 == null || t2 == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int"), t1)) {
            this.errors.add(new TypeMismatchError("int", t1.toString(), t.e1.lineNumber, t.e1.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("int"), t2)) {
            this.errors.add(new TypeMismatchError("int", t2.toString(), t.e2.lineNumber, t.e2.columnNumber));
        }
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(final ArrayLookup al) {
        SemanticChecking.Symbol.Type arrayType = al.expressionForArray.accept(this);
        SemanticChecking.Symbol.Type indexType = al.indexInArray.accept(this);
        if (arrayType == null || indexType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int[]"), arrayType)) {
            this.errors.add(new TypeMismatchError("int[]", arrayType.toString(), al.expressionForArray.lineNumber, al.expressionForArray.columnNumber));
        }
        if (!this.checkTypeEquals(new BasicType("int"), indexType)) {
            this.errors.add(new TypeMismatchError("int", indexType.toString(), al.indexInArray.lineNumber, al.indexInArray.columnNumber));
        }
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(final ArrayLength al) {
        SemanticChecking.Symbol.Type arrayType = al.expressionForArray.accept(this);
        if (arrayType == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("int[]"), arrayType)) {
            this.errors.add(new TypeMismatchError("int[]", arrayType.toString(), al.expressionForArray.lineNumber, al.expressionForArray.columnNumber));
        }
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(Call c) {
        // Check class type
        SemanticChecking.Symbol.Type ot = c.e.accept(this);
        if (ot == null) {
            this.errors.add(new AccessingNonClassError("null", c.e.lineNumber, c.e.columnNumber));
            return null;
        }
        ClassType classType = this.getClassOfObj(ot);
        if (classType == null) {
            this.errors.add(new AccessingNonClassError(ot.toString(), c.e.lineNumber, c.e.columnNumber));
            return null;
        }
        c.setReceiverClassName(classType.getName());

        // Check method type
        MethodType curMethod = this.curMethod;
        ClassType curClass = this.curClass;
        this.curMethod = null;
        this.curClass = classType;
        SemanticChecking.Symbol.Type mt = this.getTypeOfSymbol(Symbol.symbol(c.i.s));
        this.curClass = curClass;
        this.curMethod = curMethod;
        if (mt == null || !(mt instanceof MethodType)) {
            this.errors.add(new MethodNotFoundError(ot.toString(), c.i.s, c.i.lineNumber, c.i.columnNumber));
            return null;
        }
        MethodType methodType = (MethodType) mt;

        // Ensure correct number of arguments
        if (c.el.size() != methodType.getNumArgs()) {
            this.errors.add(new InvalidNumArgsError(methodType.getNumArgs(), c.el.size(), c.i.lineNumber, c.i.columnNumber));
            return null;
        }

        // Ensure arguments have same types
        for (int i = 0; i < c.el.size(); i++) {
            SemanticChecking.Symbol.Type t1 = c.el.get(i).accept(this);
            SemanticChecking.Symbol.Type t2 = methodType.getArgType(i);
            if (t1 == null || t2 == null) {
                return null;
            }
            if (!this.checkTypeEquals(t2, t1)) {
                this.errors.add(new TypeMismatchError(t2.toString(), t1.toString(), c.el.get(i).lineNumber, c.el.get(i).columnNumber));
            }
        }

        // Return method return type
        return methodType.getRetType();
    }

    public SemanticChecking.Symbol.Type visit(True t) {
        return new BasicType("boolean");
    }

    public SemanticChecking.Symbol.Type visit(False f) {
        return new BasicType("boolean");
    }

    public SemanticChecking.Symbol.Type visit(IntegerLiteral il) {
        return new BasicType("int");
    }

    public SemanticChecking.Symbol.Type visit(IdentifierExp ie) {
        SemanticChecking.Symbol.Type t = this.getTypeOfSymbol(Symbol.symbol(ie.s));
        if (t == null) {
            this.errors.add(new UndefinedSymbolError(ie.s, ie.lineNumber, ie.columnNumber));
        }
        return t;
    }

    public SemanticChecking.Symbol.Type visit(This t) {
        return new BasicType(this.curClass.getName());
    }

    public SemanticChecking.Symbol.Type visit(NewArray na) {
        return new BasicType("int[]");
    }

    public SemanticChecking.Symbol.Type visit(NewObject no) {
        if (this.symbolTable.get(Symbol.symbol(no.i.s)) == null) {
            this.errors.add(new InvalidClassError(no.i.s, no.i.lineNumber, no.i.columnNumber));
            return null;
        }
        return new BasicType(no.i.s);
    }

    public SemanticChecking.Symbol.Type visit(Not n) {
        SemanticChecking.Symbol.Type t = n.e.accept(this);
        if (t == null) {
            return null;
        }
        if (!this.checkTypeEquals(new BasicType("boolean"), t)) {
            this.errors.add(new TypeMismatchError("boolean", t.toString(), n.e.lineNumber, n.e.columnNumber));
        }
        return new BasicType("boolean");
    }

    public SemanticChecking.Symbol.Type visit(Identifier id) {
        return null;
    }

}
