package dumb;

import java.util.List;

import flowgraph.AssemFlowGraph;
import temp.Temp;
import temp.CombineMap;
import temp.TempMap;

public class DumbAlloc{
    public TempMap colors;
    int maxIter;

    public DumbAlloc(frame.Frame f, List<assem.Instr> il){
        colors = f;
        maxIter = 2;
        solve(f,il,0);
    }

    private void solve(frame.Frame f, List<assem.Instr> il, int iter){
        AssemFlowGraph fg = new AssemFlowGraph(il);
        DumbInterferenceGraph ig = new DumbInterferenceGraph(fg);
        DumbColoring dc = new DumbColoring(ig,f.registers(),colors);
        
        if(dc.spill==null || iter == maxIter){
            colors = new CombineMap(colors,dc);
        }
        else{
            f.spill(il, assem.Instr.toTempArray(dc.spill));
            solve(f, il,iter+1);
        }
    }
}