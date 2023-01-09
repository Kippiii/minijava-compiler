package StraightLineInterpretter;

import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Interprets Straight Line code blocks and counts number of print arguments
 */
class Interp {
    /**
     * Gets thrown when a class doesn't have a known subclass
     */
    static class UnknownSubclassException extends Exception {
        public UnknownSubclassException() {
            super("Unknown subclass!");
        }
    }

    /**
     * Interprets an expression and gets its value
     * @param e The expression being interpreted
     * @param vMap A variable mapping
     * @return The value of the expression
     * @throws UnknownSubclassException
     */
    static int interp(Exp e, HashMap<String, Integer> vMap) throws UnknownSubclassException {
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
                    throw new UnknownSubclassException();
            }
            return answer;
        }
        if (e instanceof EseqExp) {
            EseqExp seqE = (EseqExp) e;
            interp(seqE.stm, vMap);
            return interp(seqE.exp, vMap);
        }
        throw new UnknownSubclassException();
    }

    /**
     * Interprets a list of expressions and gets a list of values
     * @param exps The list of expressions being interpreted
     * @param vMap A variable mapping
     * @return A list of values corresponding to each expression
     * @throws UnknownSubclassException
     */
    static ArrayList<Integer> interp(ExpList exps, HashMap<String, Integer> vMap) throws UnknownSubclassException {
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
        throw new UnknownSubclassException();
    }

    /**
     * Interprets a statement with a given variable map
     * @param s The statement being interpreted
     * @param vMap The variable mapping
     * @throws UnknownSubclassException
     */
    static void interp(Stm s, HashMap<String, Integer> vMap) throws UnknownSubclassException {
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
            throw new UnknownSubclassException();
        }
    }

    /**
     * Interprets a statement starting from an empty variable mapping
     * @param s The statement being interpreted
     * @throws UnknownSubclassException
     */
    public static void interp(Stm s) throws UnknownSubclassException {
        HashMap<String, Integer> vMap = new HashMap<String, Integer>();
        interp(s, vMap);
    }


    /**
     * Counts the number of expressions in an ExpList
     * @param exps The list of expressions
     * @return The number of expressions in the list
     * @throws UnknownSubclassException
     */
    static int countargs(ExpList exps) throws UnknownSubclassException {
        if (exps instanceof PairExpList) {
            PairExpList pairExps = (PairExpList) exps;
            return 1 + countargs(pairExps.tail);
        }
        if (exps instanceof LastExpList) {
            return 1;
        }
        throw new UnknownSubclassException();
    }

    /**
     * Gets the maximum number of arguments of a print statement in the given expression
     * @param e The expression being analyzed
     * @return The maximum number of arguments of a print statement
     * @throws UnknownSubclassException
     */
    static int maxargs(Exp e) throws UnknownSubclassException {
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
        throw new UnknownSubclassException();
    }

    /**
     * Gets the maximum number of arguments in a print statement in any of the expressions in a list
     * @param exps The list of expressions
     * @return The maximum number of arguments in a print statement
     * @throws UnknownSubclassException
     */
    static int maxargs(ExpList exps) throws UnknownSubclassException {
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
        throw new UnknownSubclassException();
    }

    /**
     * Gets the maximum number of arguments in a print statement in the given statement
     * @param s The given statement
     * @return The maximum number of arguments in a print statement
     * @throws UnknownSubclassException
     */
    public static int maxargs(Stm s) throws UnknownSubclassException {
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
        throw new UnknownSubclassException();
    }

    public static void main (Stm s) throws IOException, UnknownSubclassException {
        System.out.println (maxargs(s));
        interp(s);
    }

    public static void main(String args[]) throws IOException, UnknownSubclassException {
        main (Example.a_program);
        main (Example.another_program);
    }
}