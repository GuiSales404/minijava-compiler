package frame;
import java.util.List;
import tree.*;
import assem.*;

public abstract class Frame implements temp.TempMap {
    public temp.Label name;
    public List<Access> formals;
    public abstract Frame newFrame(String name, List<Boolean> formals);
    public abstract Access allocLocal(boolean escape);
    public abstract temp.Temp FP();
    public abstract int wordSize();
    public abstract tree.CALL externalCall(String func, List<EXP> args);
    public abstract temp.Temp RV();
    public abstract String string(temp.Label label, String value);
    public abstract temp.Label badPtr();
    public abstract temp.Label badSub();
    public abstract String tempMap(temp.Temp temp);
    public abstract void procEntryExit1(List<tree.Stm> body);
    // public abstract void procEntryExit2(InstrList body);
    // public abstract void procEntryExit3(InstrList body);
    public abstract temp.Temp[] registers();
    public abstract void spill(List<assem.Instr> insns, temp.Temp[] spills);
    public abstract String programTail(); 
}
