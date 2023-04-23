package InstructionSelection;

import ErrorManagement.UnexpectedException;
import SemanticChecking.Symbol.ClassType;
import SemanticChecking.Symbol.MethodType;
import SemanticChecking.Symbol.NameSpace;
import SemanticChecking.Symbol.Symbol;
import assem.*;
import tree.*;

import java.util.*;

public class Codegen {
    private List<Instruction> insts;
    private NameSpace symbolTable;
    Set<NameOfTemp> temps;

    private void genTempSet() {
        for (Instruction inst : insts) {
            if (inst.use() != null) {
                for (NameOfTemp t : inst.use()) {
                    this.temps.add(t);
                }
            }
            if (inst.def() != null) {
                for (NameOfTemp t : inst.def()) {
                    this.temps.add(t);
                }
            }
        }
    }

    private void emit(Instruction inst) {
        this.insts.add(inst);
    }

    void munchStm(Stm s) throws UnexpectedException {
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
        } else {
            throw new IRParseException(s);
        }
    }

    void munchSeq(Stm left, Stm right) throws UnexpectedException {
        // SEQ(left, right)
        munchStm(left);
        munchStm(right);
    }

    void munchMove(MEM dst, Exp src) throws UnexpectedException {
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
                NameOfTemp e1Temp = this.munchExp(e1);
                NameOfTemp e2Temp = this.munchExp(e2);
                String comment = "[" + e1Temp.toString() + "+" + i + "] := " + e2Temp.toString();
                this.emit(new OperationInstruction("\tst `d0, [`s0+" + i + "]", comment, e2Temp, e1Temp));
                return;
            }
        }
        if (dst.exp instanceof CONST) {
            // MOVE(MEM(CONST(i)), e2)
            int i = ((CONST) dst.exp).value;
            NameOfTemp e2Temp = this.munchExp(src);
            String comment = "[" + i + "] := " + e2Temp.toString();
            this.emit(new OperationInstruction("\tst `s0, [" + i + "]", comment, null, e2Temp));
            return;
        }
        // MOVE(MEM(dst), src)
        NameOfTemp dstTemp = this.munchExp(dst);
        NameOfTemp srcTemp = this.munchExp(src);
        String comment = "[" + dstTemp.toString() + "] := " + srcTemp.toString();
        this.emit(new OperationInstruction("\tst `d0, [`s0]", comment, dstTemp, srcTemp));
    }

    void munchMove(TEMP dst, Exp src) throws UnexpectedException {
        NameOfTemp i = dst.temp;
        if (src instanceof CONST) {
            // MOVE(TEMP(i), CONST(j))
            int j = ((CONST) src).value;
            String comment = i.toString() + " := " + j;
            this.emit(new OperationInstruction("\tset " + j + ", `d0", comment, i, null));
            return;
        }
        // MOVE(TEMP(i), src)
        NameOfTemp srcTemp = this.munchExp(src);
        String comment = i.toString() + " := " + srcTemp.toString();
        this.emit(new MoveInstruction("\tmov `s0, `d0", comment, i, srcTemp));
    }

    void munchEval(Exp s) throws UnexpectedException {
        // EVAL(s)
        this.munchExp(s);
    }

    void munchJump(Exp exp, List<NameOfLabel> targets) throws UnexpectedException {
        String comment = "Unconditional GOTO";
        if (exp instanceof NAME) {
            // JUMP(NAME(label), targets)
            NAME name = (NAME) exp;
            this.emit(new OperationInstruction("\tjmp " + name.label.toString(), comment, null, null, targets));
            return;
        }
        if (exp instanceof CONST) {
            // JUMP(CONST(i), targets)
            int i = ((CONST) exp).value;
            this.emit(new OperationInstruction("\tjmp [" + i + "]", comment, null, null, targets));
            return;
        }
        // JUMP(exp, targets)
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(exp));
        this.emit(new OperationInstruction("\tjmp [`s0]", comment, null, srcTemps, targets));
    }

    void munchCjump(int rel, Exp left, Exp right, NameOfLabel ifTrue, NameOfLabel ifFalse) throws UnexpectedException {
        // Do compare operation
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        NameOfTemp leftTemp = this.munchExp(left);
        srcTemps.add(leftTemp);
        if (right instanceof CONST) {
            // right = CONST(i)
            int i = ((CONST) right).value;
            String comment = "Test " + leftTemp.toString() + " - " + i;
            this.emit(new OperationInstruction("\tcmp `s0, " + i, null, srcTemps));
        } else {
            NameOfTemp rightTemp = this.munchExp(right);
            srcTemps.add(rightTemp);
            String comment = "Test " + leftTemp.toString() + " - " + rightTemp.toString();
            this.emit(new OperationInstruction("\tcmp `s0, `s1", null, srcTemps));
        }

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
                throw new IRParseException(new CJUMP(rel, left, right, ifTrue, ifFalse));
        }
        List<NameOfLabel> jumps = new ArrayList<NameOfLabel>();
        jumps.add(ifTrue);
        jumps.add(ifFalse);
        this.emit(new OperationInstruction("\t" + relOp + " " + ifTrue.toString(), null, null, jumps));
    }

    void munchLabel(NameOfLabel lab) {
        // LABEL(lab)
        this.emit(new LabelInstruction(lab));
    }

    NameOfTemp munchExp(Exp s) throws UnexpectedException {
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
            return this.munchCall(call.func, call.args);
        } else {
            throw new IRParseException(s);
        }
    }

    NameOfTemp munchConst(int i) {
        NameOfTemp r = NameOfTemp.generateTemp();
        String comment = r.toString() + " := " + i;
        this.emit(new OperationInstruction("\tset " + i + ", `d0", comment, r, null));
        return r;
    }

    NameOfTemp munchName(NameOfLabel l) throws UnexpectedException {
        throw new IRParseException(new NAME(l));
    }

    NameOfTemp munchTemp(NameOfTemp t) {
        return t;
    }

    NameOfTemp munchBinop(int op, Exp left, Exp right) throws UnexpectedException {
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
                throw new IRParseException(new BINOP(op, left, right));
        }
        // BINOP(op, left, right)
        NameOfTemp r = NameOfTemp.generateTemp();
        List<NameOfTemp> dstTemps = new ArrayList<NameOfTemp>();
        dstTemps.add(r);
        List<NameOfTemp> srcTemps = new ArrayList<NameOfTemp>();
        srcTemps.add(this.munchExp(left));
        if (right instanceof CONST) {
            // right = CONST(i)
            int i = ((CONST) right).value;
            this.emit(new OperationInstruction("\t" + opInst + " `s0, " + i + ", `d0", dstTemps, srcTemps));
        } else {
            srcTemps.add(this.munchExp(right));
            this.emit(new OperationInstruction("\t" + opInst + " `s0, `s1, `d0", dstTemps, srcTemps));
        }
        return r;
    }

    NameOfTemp munchMem(Exp exp) throws UnexpectedException {
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
                NameOfTemp r = NameOfTemp.generateTemp();
                NameOfTemp eTemp = this.munchExp(e);
                String comment = r.toString() + " := [" + eTemp.toString() + "+" + i + "]";
                this.emit(new OperationInstruction("\tld [`s0+" + i + "], `d0", comment, r, eTemp));
                return r;
            }
        }
        if (exp instanceof CONST) {
            // MEM(CONST(i))
            int i = ((CONST) exp).value;
            NameOfTemp r = NameOfTemp.generateTemp();
            String comment = r.toString() + " := [" + i + "]";
            this.emit(new OperationInstruction("\tld [" + i + "], `d0", comment, r, null));
            return r;
        }
        NameOfTemp r = NameOfTemp.generateTemp();
        NameOfTemp expTemp = this.munchExp(exp);
        String comment = r.toString() + " := [" + expTemp.toString() + "]";
        this.emit(new OperationInstruction("\tld [`s0], `d0", comment, r, expTemp));
        return r;
    }

    NameOfTemp munchCall(Exp func, ExpList args) throws UnexpectedException {
        if (!(func instanceof NAME)) {
            throw new IRParseException(func);
        }
        NameOfLabel fLabel = ((NAME) func).label;

        int argNum = 0;
        while (args != null) {
            Exp arg = args.head;
            NameOfTemp out = new NameOfTemp("%o" + argNum);
            if (arg instanceof CONST) {
                // arg = CONST(i)
                int i = ((CONST) arg).value;
                String comment = out.toString() + " := " + i + "\t(Setting arg " + argNum + ")";
                this.emit(new OperationInstruction("\tset " + i + ", `d0", comment, out, null));
            } else {
                NameOfTemp argTemp = this.munchExp(arg);
                String comment = out.toString() + " := " + argTemp.toString() + "\t(Setting arg " + argNum + ")";
                this.emit(new MoveInstruction("\tmov `s0, `d0", comment, out, argTemp));
            }
            args = args.tail;
            argNum++;
        }

        this.emit(new OperationInstruction("\tcall " + fLabel.toString()));
        return new NameOfTemp("%o0");
    }

    private MethodType getMethodType(Symbol name) {
        int dollarIndex = name.toString().indexOf("$");
        Symbol className = Symbol.symbol(name.toString().substring(0, dollarIndex));
        Symbol methodName = Symbol.symbol(name.toString().substring(dollarIndex+1));
        return ((ClassType) this.symbolTable.getType(className)).getMethodType(methodName);
    }

    void addPrologue(Symbol name) {
        MethodType mt = this.getMethodType(name);
        boolean isMain = mt.getMain();

        if (!isMain) {
            this.insts.add(0, new OperationInstruction("\tsave    %sp, -4*(LOCLS+TEMPS+ARGSB+1+16)&-8, %sp"));
            this.insts.add(0, new OperationInstruction("\t.set ARGSB, 0"));
            this.insts.add(0, new OperationInstruction("\t.set TEMPS, " + this.temps.size()));
            this.insts.add(0, new OperationInstruction("\t.set LOCLS, " + mt.getNumLocals()));
        }
        this.insts.add(0, new LabelInstruction(new NameOfLabel(name.toString())));
        if (isMain) {
            this.insts.add(0, new LabelInstruction(new NameOfLabel("start")));
            this.insts.add(0, new OperationInstruction("\t.global start"));
            this.insts.add(0, new Comment("main procedure definition " + name.toString()));
        } else {
            this.insts.add(0, new Comment("procedure definition " + name.toString() + ": formals=" + (mt.getNumArgs()+1) + ", locals=" + mt.getNumLocals()));
        }
    }

    void addEpilogue(Symbol name) {
        MethodType mt = this.getMethodType(name);
        boolean isMain = mt.getMain();

        this.emit(new LabelInstruction(new NameOfLabel(name.toString() + "$epilogBegin")));
        if (isMain) {
            this.emit(new OperationInstruction("\tclr %o0"));
            this.emit(new OperationInstruction("\tcall exit"));
            this.emit(new OperationInstruction("\tnop"));
        } else {
            this.emit(new OperationInstruction("\tret"));
            this.emit(new OperationInstruction("\trestore"));
        }
    }

    List<Instruction> codegen(Symbol name, List<Stm> stms, NameSpace symbolTable) throws UnexpectedException {
        this.insts = new ArrayList<Instruction>();
        this.symbolTable = symbolTable;
        this.temps = new HashSet<NameOfTemp>();
        for (Stm s : stms) {
            this.munchStm(s);
        }
        this.genTempSet();
        this.addPrologue(name);
        this.addEpilogue(name);
        return this.insts;
    }

}
