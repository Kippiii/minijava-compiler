package StraightLineInterpretter;

import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;
import java.util.ArrayList;

class Interp {
    static class UnknownSubclassExcpetion extends Exception {
        public UnknownSubclassExcpetion() {
            super("Unknown subclass!");
        }
    }
    static int interp(Exp e, HashMap<String, Integer> vMap) throws UnknownSubclassExcpetion {
        if (e instanceof IdExp) {
            IdExp idE = (IdExp) e;
            return vMap.get(idE.id);
        }
        if (e instanceof NumExp) {
            NumExp numE = (NumExp) e;
            return numE.num;
        }
        if (e instanceof OpExp) {
            OpExp opE = (OpExp) e;
            int val1 = interp(opE.left, vMap);
            int val2 = interp(opE.right, vMap);
            int answer;
            switch (opE.oper) {
                case OpExp.Plus:
                    answer = val1 + val2;
                    break;
                case OpExp.Minus:
                    answer = val1 - val2;
                    break;
                case OpExp.Times:
                    answer = val1 * val2;
                    break;
                case OpExp.Div:
                    answer = val1 / val2;
                    break;
                default:
                    throw new UnknownSubclassExcpetion();
            }
            return answer;
        }
        if (e instanceof EseqExp) {
            EseqExp seqE = (EseqExp) e;
            interp(seqE.stm, vMap);
            return interp(seqE.exp, vMap);
        }
        throw new UnknownSubclassExcpetion();
    }
    static ArrayList<Integer> interp(ExpList exps, HashMap<String, Integer> vMap) throws UnknownSubclassExcpetion {
        if (exps instanceof PairExpList) {
            PairExpList pairExps = (PairExpList) exps;
            int newVal = interp(pairExps.head, vMap);
            ArrayList<Integer> values = interp(pairExps.tail, vMap);
            values.add(newVal);
            return values;
        }
        if (exps instanceof LastExpList) {
            LastExpList lastExps = (LastExpList) exps;
            ArrayList<Integer> values = new ArrayList<Integer>();
            int newVal = interp(lastExps.head, vMap);
            values.add(newVal);
            return values;
        }
        throw new UnknownSubclassExcpetion();
    }
    static void interp(Stm s, HashMap<String, Integer> vMap) throws UnknownSubclassExcpetion {
        if (s instanceof CompoundStm) {
            CompoundStm compS = (CompoundStm) s;
            interp(compS.stm1, vMap);
            interp(compS.stm2, vMap);
        } else if (s instanceof AssignStm) {
            AssignStm assignS = (AssignStm) s;
            int value = interp(assignS.exp, vMap);
            vMap.put(assignS.id, value);
        } else if (s instanceof PrintStm) {
            PrintStm printS = (PrintStm) s;
            ArrayList<Integer> values = interp(printS.exps, vMap);
            for (int i = values.size() - 1; i > 0; i--) {
                System.out.printf("%d ", values.get(i));
            }
            System.out.printf("%d%n", values.get(0));
        } else {
            throw new UnknownSubclassExcpetion();
        }
    }
    public static void interp(Stm s) throws UnknownSubclassExcpetion {
        HashMap<String, Integer> vMap = new HashMap<String, Integer>();
        interp(s, vMap);
    }


    static int countargs(ExpList exps) throws UnknownSubclassExcpetion {
        if (exps instanceof PairExpList) {
            PairExpList pairExps = (PairExpList) exps;
            return 1 + countargs(pairExps.tail);
        }
        if (exps instanceof LastExpList) {
            return 1;
        }
        throw new UnknownSubclassExcpetion();
    }
    static int maxargs(Exp e) throws UnknownSubclassExcpetion {
        if (e instanceof IdExp) {
            return 0;
        }
        if (e instanceof NumExp) {
            return 0;
        }
        if (e instanceof OpExp) {
            OpExp opE = (OpExp) e;
            int leftMax = maxargs(opE.left);
            int rightMax = maxargs(opE.right);
            return Math.max(leftMax, rightMax);
        }
        if (e instanceof EseqExp) {
            EseqExp seqE = (EseqExp) e;
            int stmMax = maxargs(seqE.stm);
            int expMax = maxargs(seqE.exp);
            return Math.max(stmMax, expMax);
        }
        throw new UnknownSubclassExcpetion();
    }
    static int maxargs(ExpList exps) throws UnknownSubclassExcpetion {
        if (exps instanceof PairExpList) {
            PairExpList pairExps = (PairExpList) exps;
            int headMax = maxargs(pairExps.head);
            int tailMax = maxargs(pairExps.tail);
            return Math.max(headMax, tailMax);
        }
        if (exps instanceof LastExpList) {
            LastExpList lastExps = (LastExpList) exps;
            return maxargs(lastExps.head);
        }
        throw new UnknownSubclassExcpetion();
    }
    public static int maxargs(Stm s) throws UnknownSubclassExcpetion {
        if (s instanceof CompoundStm) {
            CompoundStm compS = (CompoundStm) s;
            int max1 = maxargs(compS.stm1);
            int max2 = maxargs(compS.stm2);
            return Math.max(max1, max2);
        }
        if (s instanceof AssignStm) {
            AssignStm assignS = (AssignStm) s;
            return maxargs(assignS.exp);
        }
        if (s instanceof PrintStm) {
            PrintStm printS = (PrintStm) s;
            int argCount = countargs(printS.exps);
            int mostArgs = maxargs(printS.exps);
            return Math.max(argCount, mostArgs);
        }
        throw new UnknownSubclassExcpetion();
    }

    public static void main (Stm s) throws IOException, UnknownSubclassExcpetion {
        System.out.println (maxargs(s));
        interp(s);
    }

    public static void main(String args[]) throws IOException, UnknownSubclassExcpetion {
        main (Example.a_program);
        main (Example.another_program);
    }
}