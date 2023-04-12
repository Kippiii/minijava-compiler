package InstructionSelection;

import assem.LabelInstruction;
import assem.OperationInstruction;
import tree.*;

import java.util.ArrayList;
import java.util.List;

public class Codegen {

    void munchStm(Stm s) {
        if (s instanceof SEQ) {
            SEQ seq = (SEQ) s;
            this.munchSeq(seq.left, seq.right);
        } else if (s instanceof MOVE) {
            MOVE move = (MOVE) s;
            if (move.dst instanceof MEM)
                this.munchMove((MEM) move.dst, move.src);
            else if (move.dst instanceof TEMP)
                this.munchMove((TEMP) move.dst, move.src);
        } else if (s instanceof EVAL) {
            EVAL eval = (EVAL) s;
            this.munchEval(eval.exp);
        } else if (s instanceof JUMP) {
            JUMP jump = (JUMP) s;
            this.munchJump(jump.exp, jump.targets);
        } else if (s instanceof CJUMP) {
            CJUMP cjump = (CJUMP) s;
            this.munchCjump(cjump.relop, cjump.left, cjump.right, cjump.iftrue, cjump.iffalse);
        } else if (s instanceof LABEL) {
            LABEL label = (LABEL) s;
            this.munchLabel(label.label);
        }
        // TODO ERROR
    }

    void munchSeq(Stm left, Stm right) {
        // SEQ(left, right)
        munchStm(left);
        munchStm(right);
    }

    void munchMove(MEM dst, Exp src) {
        if (dst.exp instanceof BINOP) {
            BINOP binop = (BINOP) dst.exp;
            if (binop.binop == BINOP.PLUS && (binop.left instanceof CONST || binop.right instanceof CONST)) {
                // MOVE(MEM(BINOP(PLUS, e1, CONST(i))), e2) || MOVE(MEM(BINOP(PLUS, CONST(i), e1)), e2)
                Exp e1;
                Exp e2 = src;
                int i;
                if (binop.left instanceof CONST) {
                    e1 = binop.right;
                    i = ((CONST) binop.left).value;
                } else {
                    e1 = binop.left;
                    i = ((CONST) binop.right).value;
                }
                List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
                srcTemps.add(this.munchExp(e1));
                srcTemps.add(this.munchExp(e2));
                this.emit(new OperationInstruction("STORE M[`s0+" + i + "] <- `s1\n", null, srcTemps));
                return;
            }
        }
        if (src instanceof MEM) {
            // MOVE(MEM(e1), MEM(e2))
            MEM mem = (MEM) src;
            Exp e1 = dst.exp;
            Exp e2 = mem.exp;
            List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
            srcTemps.add(this.munchExp(e1));
            srcTemps.add(this.munchExp(e2));
            this.emit(new OperationInstruction("MOVE M[`s0] <- M[`s1]\n", null, srcTemps));
            return;
        }
        if (dst.exp instanceof CONST) {
            // MOVE(MEM(CONST(i)), e2)
            int i = ((CONST) dst.exp).value;
            Exp e2 = src;
            List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
            srcTemps.add(this.munchExp(src));
            this.emit(new OperationInstruction("STORE M[r0+" + i + "] <- `s0\n", null, srcTemps));
            return;
        }
        // MOVE(MEM(dst), src)
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(dst));
        srcTemps.add(this.munchExp(src));
        this.emit(new OperationInstruction("STORE M[`s0] <- `s1\n", null, srcTemps));
    }

    void munchMove(TEMP dst, Exp src) {
        // MOVE(TEMP(i), src)
        NameOfTemp i = dst.temp;
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(i);
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(src));
        this.emit(new OperationInstruction("ADD `d0 <- `s0 + r0\n", dstTemps, srcTemps));
    }

    void munchEval(Exp s) {
        // TODO
    }

    void munchJump(Exp exp, List<NameOfLabel> targets) {
        // TODO
    }

    void munchCjump(int rel, Exp left, Exp right, NameOfLabel ifTrue, NameOfLabel ifFalse) {
        // TODO
    }

    void munchLabel(NameOfLabel lab) {
        // LABEL(lab)
        this.emit(new LabelInstruction(lab.toString() + ":\n", lab));
    }


    // TODO Make recursive

    NameOfTemp munchExp(Exp s) {
        if (s instanceof CONST) {
            CONST c = (CONST) s;
            return this.munchConst(c.value);
        } else if (s instanceof NAME) {
            NAME name = (NAME) s;
            return this.munchName(name.label);
        } else if (s instanceof TEMP) {
            TEMP temp = (TEMP) s;
            return this.munchTemp(temp.temp);
        } else if (s instanceof BINOP) {
            BINOP binop = (BINOP) s;
            return this.munchBinop(binop.binop, binop.left, binop.right);
        } else if (s instanceof MEM) {
            MEM mem = (MEM) s;
            return this.munchMem(mem.exp);
        } else if (s instanceof CALL) {
            CALL call = (CALL) s;
            this.munchCall(call.func, call.args);
        }
        return null; // TODO ERROR
    }

    NameOfTemp munchConst(int i) {
        NameOfTemp r = new NameOfTemp();
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(r);
        this.emit("ADDI `d0 <- r0+" + i + "\n", dstTemps, null);
        return r;
    }

    NameOfTemp munchName(NameOfLabel l) {
        return null; // TODO
    }

    NameOfTemp munchTemp(NameOfTemp t) {
        return t;
    }

    NameOfTemp munchBinop(int op, Exp left, Exp right) {
        switch (op) {
            case BINOP.PLUS:
                if (left instanceof CONST || right instanceof CONST) {
                    // BINOP(PLUS, CONST(i), e) || BINOP(PLUS, e, CONST(i))
                    int i;
                    Exp e;
                    if (left instanceof CONST) {
                        i = ((CONST) left).value;
                        e = right;
                    } else {
                        i = ((CONST) right).value;
                        e = left;
                    }
                    NameOfTemp r = new NameOfTemp();
                    List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
                    dstTemps.add(r);
                    List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
                    srcTemps.add(this.munchExp(e));
                    this.emit("ADDI `d0 <- `s0+" + i + "\n", dstTemps, srcTemps);
                    return r;
                }
                NameOfTemp r = new NameOfTemp();
                List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
                dstTemps.add(r);
                List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
                srcTemps.add(this.munchExp(left));
                srcTemps.add(this.munchExp(right));
                this.emit("ADD `d0 <- `s0+`s1\n", dstTemps, srcTemps);
                return r;
            break;
            case BINOP.MINUS:
                // TODO
                break;
            case BINOP.MUL:
                // TODO
                break;
            case BINOP.DIV:
                // TODO
                break;
            case BINOP.AND:
                // TODO
                break;
            case BINOP.OR:
                // TODO
                break;
            case BINOP.LSHIFT:
                // TODO
                break;
            case BINOP.RSHIFT:
                // TODO
                break;
            case BINOP.ARSHIFT:
                // TODO
                break;
            case BINOP.XOR:
                // TODO
                break;
        }
        return null; // TODO ERROR
    }

    NameOfTemp munchMem(Exp exp) {
        if (exp instanceof BINOP) {
            BINOP binop = (BINOP) exp;
            if (binop.binop == BINOP.PLUS && (binop.left instanceof CONST || binop.right instanceof CONST)) {
                // MEM(BINOP(PLUS, CONST(i), e)) || MEM(BINOP(PLUS, e, CONST(i)))
                int i;
                Exp e;
                if (binop.left instanceof CONST) {
                    i = ((CONST) binop.left).value;
                    e = binop.right;
                } else {
                    i = ((CONST) binop.right).value;
                    e = binop.left;
                }
                NameOfTemp r = new NameOfTemp();
                List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
                dstTemps.add(r);
                List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
                srcTemps.add(this.munchExp(e));
                this.emit(new OperationInstruction("LOAD `d0 <- M[`s0+" + i + "]\n", dstTemps, srcTemps));
                return r;
            }
        }
        if (exp instanceof CONST) {
            // MEM(CONST(i))
            int i = ((CONST) exp).value;
            NameOfTemp r = new NameOfTemp();
            List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
            this.emit(new OperationInstruction("LOAD `d0 <- M[r0 +" + i + "]\n", dstTemps, null));
            return r;
        }
        NameOfTemp r = new NameOfTemp();
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(r);
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(exp));
        this.emit(new OperationInstruction("LOAD `d0 <- M[`s0+0]\n", dstTemps, srcTemps));
        return r;
    }

    NameOfTemp munchCall(Exp func, ExpList args) {
        return null; // TODO
    }

}
