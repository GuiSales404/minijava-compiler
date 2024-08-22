package assem;

import tree.*;
//import frame.Frame;
import mips.MipsFrame;
import temp.*;
import java.util.*;

public class Codegen {
    MipsFrame frame;
    private ListIterator<Instr> insns;

    public Codegen(MipsFrame f, ListIterator<Instr> i) {
        frame = f;
        insns = i;
    }

    private InstrList ilist = null, last = null;

    private void emit(Instr inst) {
        insns.add(inst);
        if (last != null) {
            last = last.tail = new InstrList(inst, null);
        } else {
            last = ilist = new InstrList(inst, null);
        }
    }

    public InstrList codegen(tree.Stm s) {
        InstrList l;
        munchStm(s);
        l = ilist;
        ilist = last = null;
        return l;
    }

    void munchStm(tree.Stm s) {
        if (s instanceof tree.SEQ) {
            munchStm(((tree.SEQ) s).left);
            munchStm(((tree.SEQ) s).right);
        } else if (s instanceof tree.MOVE) {
            munchMOVE((tree.MOVE) s);
        } else if (s instanceof tree.LABEL) {
            munchLABEL((tree.LABEL) s);
        } else if (s instanceof tree.CJUMP) {
            munchCJUMP((tree.CJUMP) s);
        } else if (s instanceof tree.JUMP) {
            munchJUMP((tree.JUMP) s);
        } else if (s instanceof tree.EXPR) {
            munchCALL((tree.CALL) ((tree.EXPR) s).exp);
        }
    }

    void munchMOVE(tree.MOVE s) {
        if ((s.dst instanceof tree.MEM) && (s.src instanceof tree.MEM)) {
            temp.Temp temp_dst = munchExp(((tree.MEM) s.dst).exp);
            temp.Temp temp_src = munchExp(((tree.MEM) s.src).exp);
            TempList tail = new TempList(temp_src, null);
            TempList head = new TempList(temp_dst, tail);
            OPER op = new OPER("move $t0,$t2\n", null, head);
            emit(op);
        } else if ((s.dst instanceof tree.MEM) && (s.src instanceof tree.EXP)) {
            if (((tree.MEM) s.dst).exp instanceof tree.BINOP) {

                if (((tree.BINOP) ((tree.MEM) s.dst).exp).binop == BINOP.PLUS) {
                    if (((tree.BINOP) ((tree.MEM) s.dst).exp).right instanceof tree.CONST) {
                        temp.Temp t1 = munchExp(((tree.BINOP) ((tree.MEM) s.dst).exp).left);
                        temp.Temp t2 = munchExp(s.src);
                        temp.TempList l1 = new TempList(t1, null);
                        temp.TempList l2 = new TempList(t2, null);
                        assem.OPER op = new OPER("move $t0,$t2\n", l1, l2);
                        emit(op);
                    } else if (((tree.BINOP) ((tree.MEM) s.dst).exp).left instanceof tree.CONST) {
                        temp.Temp t2 = munchExp(s.src);
                        temp.TempList l2 = new TempList(t2, null);
                        temp.Temp t1 = munchExp(((tree.BINOP) ((tree.MEM) s.dst).exp).right);
                        temp.TempList l1 = new TempList(t1, null);
                        assem.OPER op = new OPER("move $t0,$t2\n", l1, l2);
                        emit(op);
                    }
                }

            } else if (((tree.MEM) s.dst).exp instanceof tree.CONST) {
                TempList l = new TempList(munchExp(s.src), null);
                OPER op = new OPER("sw$t0, " + ((tree.CONST) ((tree.MEM) s.dst).exp).value + "($zero)\n", null, l);
                emit(op);
            } else {
                TempList tail = new TempList(munchExp(s.src), null);
                TempList head = new TempList(munchExp(((tree.MEM) s.dst).exp), tail);
                OPER op = new OPER("sw$t1, 0($t0)\n", null, head);
                emit(op);
            }
        }

        else {
            TempList l1 = new TempList(((tree.TEMP) s.dst).temp, null);
            TempList l2 = new TempList(munchExp(s.src), null);
            OPER op = new OPER("add $s0,$t0,$r0\n", l1, l2);
            emit(op);
        }
    }

    void munchLABEL(tree.Stm s) {
        emit(new LABEL(((tree.LABEL) s).label.toString() + ":", ((tree.LABEL) s).label));
    }

    void munchCJUMP(tree.CJUMP s) {
        String relop = "";
        switch (s.relop) {
            case 0:
                relop = "beq";
                break;
            case 2:
                relop = "blt";
                break;
        }
        temp.Temp l = munchExp(s.left);
        temp.Temp r = munchExp(s.right);
        TempList l1 = new TempList(l, new TempList(r, null));
        temp.LabelList ll = new temp.LabelList(s.iftrue, new temp.LabelList(s.iffalse, null));
        OPER op = new OPER(relop + " $s0,$s1,$d0\n", null, l1, ll);
        emit(op);
    }

    void munchJUMP(tree.Stm s) {
        OPER op = new OPER("j j0\n", null, null, ((tree.JUMP) s).targets);
        emit(op);
    }

    void munchCALL(tree.CALL s) {
        temp.Temp r = munchExp(s.func);
        temp.TempList l = munchARGS(0, s.args);
        NAME name = (tree.NAME) s.func;
        temp.TempList l1 = new TempList(r, l);

        OPER op = new OPER("jal " + name.label + "\n", null, l1);

        emit(op);
    }

    temp.TempList munchARGS(int i, List<EXP> args) {
        if (args.isEmpty()) {
            return new temp.TempList(null, null);
        } else {
            tree.EXP e = args.remove(0);
            return new temp.TempList(munchExp(e), munchARGS(0, args));
        }
    }

    temp.Temp munchExp(tree.EXP e) {
        if (e instanceof tree.MEM) {
            return munchMEM((tree.MEM) e);
        } else if (e instanceof tree.BINOP) {
            return munchBINOP((tree.BINOP) e);
        } else if (e instanceof tree.CONST) {
            return munchCONST((tree.CONST) e);
        } else if (e instanceof tree.TEMP) {
            return munchTEMP((tree.TEMP) e);
        }
        return null;
    }

    temp.Temp munchMEM(tree.MEM e) {
        temp.Temp t = new temp.Temp();
        TempList l1 = new TempList(t, null);

        if (e.exp instanceof tree.BINOP && ((tree.BINOP) e.exp).binop == tree.BINOP.PLUS) {
            tree.EXP r = ((tree.BINOP) e.exp).right;
            tree.EXP l = ((tree.BINOP) e.exp).left;

            if (r instanceof tree.CONST) {
                TempList s = new TempList(munchExp(l), null);
                emit(new OPER("lw d0," + ((tree.CONST) r).value + "(s0)\n", l1, s));
            } else if (l instanceof tree.CONST) {
                TempList s = new TempList(munchExp(r), null);
                emit(new OPER("lw d0," + ((tree.CONST) l).value + "(s0) \n", l1, s));
            }

        } else if (e.exp instanceof tree.CONST) {
            tree.CONST c = (tree.CONST) e.exp;
            emit(new OPER("lw d0, " + c.value + "($zero) \n", l1, null));
        } else {
            emit(new OPER("lw d0,s0\n", l1, new TempList(munchExp(e.exp), null)));
        }
        return t;
    }

    temp.Temp munchBINOP(tree.BINOP e) {
        temp.Temp r = new temp.Temp();
        TempList l1 = new TempList(r, null);

        if (e.binop == tree.BINOP.PLUS) {
            if (e.left instanceof tree.EXP) {
                if (e.right instanceof tree.CONST) {
                    TempList l2 = new TempList(munchExp(e.left), null);
                    OPER op = new OPER("addi d0,s0," + ((CONST) e.right).value + "\n", l1, l2);
                    emit(op);
                } else {
                    TempList l3 = new TempList(munchExp(e.right), null);
                    TempList l2 = new TempList(munchExp(e.left), l3);
                    OPER op = new OPER("add d0,s0,s1\n", l1, l2);
                    emit(op);
                }
            } else {
                TempList l2 = new TempList(munchExp(e.right), null);
                OPER op = new OPER("addi d0,s0," + ((CONST) e.left).value + "\n", l1, l2);
                emit(op);
            }
        } else if (e.binop == tree.BINOP.MINUS) {
            TempList l3 = new TempList(munchExp(e.right), null);
            TempList l2 = new TempList(munchExp(e.left), l3);
            OPER op = new OPER("sub d0,s1,s2\n", l1, l2);
            emit(op);
        } else if (e.binop == tree.BINOP.DIV) {
            TempList l3 = new TempList(munchExp(e.right), null);
            TempList l2 = new TempList(munchExp(e.left), l3);
            OPER op = new OPER("div s1,s2\n", null, l2);
            emit(op);
        } else if (e.binop == tree.BINOP.MUL) {
            TempList l3 = new TempList(munchExp(e.right), null);
            TempList l2 = new TempList(munchExp(e.left), l3);
            OPER op = new OPER("mul d0,s1,s2\n", l1, l2);
            emit(op);
        } else {
            TempList l3 = new TempList(munchExp(e.right), null);
            TempList l2 = new TempList(munchExp(e.left), l3);
            OPER op = new OPER("and d0,s1,s2\n", l1, l2);
            emit(op);
        }
        return r;
    }

    temp.Temp munchCONST(tree.CONST e) {
        temp.Temp r = new temp.Temp();
        TempList l = new TempList(r, null);
        OPER op = new OPER("addi d0,r0+" + e.value + "\n", null, l);
        return r;
    }

    temp.Temp munchTEMP(tree.TEMP e) {
        return e.temp;
    }
}