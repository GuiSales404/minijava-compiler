package regalloc;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import flowgraph.AssemFlowGraph;
import flowgraph.Liveness;
import temp.Temp;

public class RegAlloc implements temp.TempMap {
    frame.Frame f;
    List<assem.Instr> il;
    List<Temp> simplifyWorklist, freezeWorklist, spillWorklist, initial;
    int K;

    HashMap<Temp,String> tempMap;

    public String tempMap(temp.Temp temp){
        if(f.tempMap(temp)!=null)
            return f.tempMap(temp);
        return tempMap.get(temp);
    }

    public RegAlloc(frame.Frame f, List<assem.Instr> il){
        this.f = f;
        this.il = il;
        Main();
    }   

    public void Main(){
        
        AssemFlowGraph fg = new AssemFlowGraph(il);
        
        InterferenceGraph ig = new InterferenceGraph(fg,this);

        Liveness lv = ig.lv;

        MakeWorklist(ig);

        for(int iter=0, maxIter=5;iter < maxIter;iter++){
            if(simplifyWorklist.size()!=0){
                Simplify();
            }
            else if(freezeWorklist.size()!=0){
                //Freeze();
            }
            else if(spillWorklist.size()!=0){
                //SelectSpill();
            }
        }
    }

    boolean MoveRelated(Temp t, InterferenceGraph ig){
        return ig.MoveList.get(t)!=null;
    }

    public void MakeWorklist(InterferenceGraph ig){
        initial = new LinkedList<Temp>();
        for (Temp temp : ig.mp.keySet()) {
            if(tempMap(temp)==null)
                initial.add(temp);
        }

        simplifyWorklist = new LinkedList<Temp>();
        freezeWorklist = new LinkedList<Temp>();
        spillWorklist = new LinkedList<Temp>();

        for (Temp temp : initial) {
            if(ig.degree.get(ig.mp.get(temp))>=K){
                spillWorklist.add(temp);
            }
            else if(MoveRelated(temp, ig)){
                freezeWorklist.add(temp);
            }
            else{
                simplifyWorklist.add(temp);
            }
        }
    }

    void Simplify(){
        Temp t1 = simplifyWorklist.get(0);
        simplifyWorklist.remove(0);

    }
}
