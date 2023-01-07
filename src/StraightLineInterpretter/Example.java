package StraightLineInterpretter;

class Example {
    // Stm :: a:=5+3; b := (print (a, a-1), 10*a); print(b)
    // Out ::
    //        8 7
    //        80
    static Stm a_program =
            new CompoundStm(new AssignStm("a",new OpExp(new NumExp(5), OpExp.Plus, new NumExp(3))),
                    new CompoundStm(new AssignStm("b",
                            new EseqExp(new PrintStm(new PairExpList(new IdExp("a"),
                                    new LastExpList(new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(1))))),
                                    new OpExp(new NumExp(10), OpExp.Times, new IdExp("a")))),
                            new PrintStm(new LastExpList(new IdExp("b")))));

    // Stm :: print ( (a := (print (7, 8, 9, 10), 7), a+8) , a/3 ); print ((b := 15, 3), b-a)
    // Out ::
    //        7 8 9 10
    //        15 2
    //        3 8
    static Stm another_program =
            new CompoundStm(
                    new PrintStm(
                            new PairExpList(
                                    new EseqExp(
                                            new AssignStm("a",
                                                    new EseqExp(
                                                            new PrintStm(
                                                                    new PairExpList(
                                                                            new NumExp(7),
                                                                            new PairExpList(
                                                                                    new NumExp(8),
                                                                                    new PairExpList(
                                                                                            new NumExp(9),
                                                                                            new LastExpList(new NumExp(10))
                                                                                    )
                                                                            )
                                                                    )
                                                            ),
                                                            new NumExp(7)
                                                    )
                                            ),
                                            new OpExp(new IdExp("a"), OpExp.Plus, new NumExp(8))
                                    ),
                                    new LastExpList(
                                            new OpExp(new IdExp("a"), OpExp.Div, new NumExp(3))
                                    )
                            )
                    ),
                    new PrintStm(
                            new PairExpList(
                                    new EseqExp(
                                            new AssignStm("b", new NumExp(15)),
                                            new NumExp(3)
                                    ),
                                    new LastExpList(
                                            new OpExp(new IdExp("b"), OpExp.Minus, new IdExp("a"))
                                    )
                            )
                    )
            );
}