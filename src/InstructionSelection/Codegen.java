package InstructionSelection;

import assem.LabelInstruction;
import assem.MoveInstruction;
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
                this.emit(new OperationInstruction("st `s1, [`s0+" + i + "]\n", null, srcTemps));
                return;
            }
        }
        if (dst.exp instanceof CONST) {
            // MOVE(MEM(CONST(i)), e2)
            int i = ((CONST) dst.exp).value;
            Exp e2 = src;
            List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
            srcTemps.add(this.munchExp(src));
            this.emit(new OperationInstruction("st `s0, [" + i + "]\n", null, srcTemps));
            return;
        }
        // MOVE(MEM(dst), src)
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(dst));
        srcTemps.add(this.munchExp(src));
        this.emit(new OperationInstruction("st `s1, [`s0]\n", null, srcTemps));
    }

    void munchMove(TEMP dst, Exp src) {
        // MOVE(TEMP(i), src)
        NameOfTemp i = dst.temp;
        this.emit(new MoveInstruction("mov `s0, `d0\n", i, this.munchExp(src)));
    }

    void munchEval(Exp s) {
        // EVAL(s)
        this.munchExp(s);
    }

    void munchJump(Exp exp, List<NameOfLabel> targets) {
        if (exp instanceof NAME) {
            // JUMP(NAME(label), targets)
            NAME name = (NAME) exp;
            this.emit(new OperationInstruction("jmp " + name.label.toString() + "\n", null, null, targets));
            return;
        }
        if (exp instanceof CONST) {
            // JUMP(CONST(i), targets)
            int i = ((CONST) exp).value;
            this.emit(new OperationInstruction("jmp [" + i + "]\n", null, null, targets));
            return;
        }
        // JUMP(exp, targets)
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(exp));
        this.emit(new OperationInstruction("jmp [`s0]\n", null, srcTemps, targets));
    }

    void munchCjump(int rel, Exp left, Exp right, NameOfLabel ifTrue, NameOfLabel ifFalse) {
        // Do compare operation
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(left));
        srcTemps.add(this.munchExp(right));
        this.emit("cmp `s0, `s1\n", null, srcTemps);

        // Create jump if true
        String relOp;
        switch (rel) {
            case CJUMP.EQ:
                relOp = "be";
                break;
            case CJUMP.NE:
                relOp = "bne";
                break;
            case CJUMP.LT:
                relOp = "bl";
                break;
            case CJUMP.GT:
                relOp = "bg";
                break;
            case CJUMP.LE:
                relOp = "ble";
                break;
            case CJUMP.GE:
                relOp = "bge";
                break;
            case CJUMP.ULT:
                relOp = "bcs";
                break;
            case CJUMP.UGT:
                relOp = "bgu";
                break;
            case CJUMP.ULE:
                relOp = "bleu";
                break;
            case CJUMP.UGE:
                relOp = "bcc";
                break;
            default:
                return; // TODO ERROR
        }
        List<NameOfLabel> jumps = new ArrayList<NameOfLabel>();
        jumps.add(ifTrue);
        jumps.add(ifFalse);
        this.emit(new OperationInstruction(relOp + " " + ifTrue.toString() + "\n", null, null, jumps));
    }

    void munchLabel(NameOfLabel lab) {
        // LABEL(lab)
        this.emit(new LabelInstruction(lab));
    }

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
        this.emit(new OperationInstruction("mov " + i + ", `d0\n", dstTemps, null));
        return r;
    }

    NameOfTemp munchName(NameOfLabel l) {
        return null; // TODO ERROR
    }

    NameOfTemp munchTemp(NameOfTemp t) {
        return t;
    }

    NameOfTemp munchBinop(int op, Exp left, Exp right) {
        String opInst;
        switch (op) {
            case BINOP.PLUS:
                // BINOP(PLUS, left, right)
                opInst = "add";
                break;
            case BINOP.MINUS:
                // BINOP(MINUS, left, right)
                opInst = "sub";
                break;
            case BINOP.MUL:
                opInst = "smul";
                break;
            case BINOP.DIV:
                opInst = "sdiv";
                break;
            case BINOP.AND:
                opInst = "and";
                break;
            case BINOP.OR:
                opInst = "or";
                break;
            case BINOP.LSHIFT:
                opInst = "sll";
                break;
            case BINOP.RSHIFT:
                opInst = "srl";
                break;
            case BINOP.ARSHIFT:
                opInst = "sra";
                break;
            case BINOP.XOR:
                opInst = "xor";
                break;
            default:
                return null; // TODO ERROR
        }
        // BINOP(op, left, right)
        NameOfTemp r = new NameOfTemp();
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(r);
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(left));
        srcTemps.add(this.munchExp(right));
        this.emit(opInst + " `s0, `s1, `d0\n", dstTemps, srcTemps);
        return r;
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
                this.emit(new OperationInstruction("ld [`s0+" + i + "], `d0\n", dstTemps, srcTemps));
                return r;
            }
        }
        if (exp instanceof CONST) {
            // MEM(CONST(i))
            int i = ((CONST) exp).value;
            NameOfTemp r = new NameOfTemp();
            List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
            this.emit(new OperationInstruction("ld [" + i + "], `d0\n", dstTemps, null));
            return r;
        }
        NameOfTemp r = new NameOfTemp();
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(r);
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(exp));
        this.emit(new OperationInstruction("ld [`s0], `d0\n", dstTemps, srcTemps));
        return r;
    }

    NameOfTemp munchCall(Exp func, ExpList args) {
        return null; // TODO
    }

}
