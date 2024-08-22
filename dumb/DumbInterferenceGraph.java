package dumb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import flowgraph.AssemFlowGraph;
import flowgraph.Liveness;

import graph.Node;
import graph.NodeList;

import temp.Temp;
import temp.TempList;

public class DumbInterferenceGraph {
    public HashSet<Temp> vertices;
    public HashMap<Temp,List<Temp>> adj;
    public HashMap<Temp,Integer> dg;

    void addEdge(Temp from, Temp to){
        if(adj.get(from).contains(to)==false){
            adj.get(from).add(to);
            adj.get(to).add(from);

            dg.put(to, dg.get(to)+1);
            dg.put(from, dg.get(from)+1);
        }
    }
    
    public DumbInterferenceGraph(AssemFlowGraph fg){
        
        vertices = new HashSet<Temp>();
        adj = new HashMap<Temp,List<Temp>>();
        dg = new HashMap<Temp,Integer>();
        

        Liveness lv = new Liveness(fg);

        // Create nodes
        
        List<Node> nodes = NodeList.toList(fg.nodes());
        
        for (Node node : nodes) {
            assem.Instr instr = fg.mp.get(node);
            
            List<Temp> use = TempList.toList(instr.use());
            if(use==null) use = new LinkedList<>();
            for (Temp t : use) 
                vertices.add(t);
                    
            List<Temp> def = TempList.toList(instr.def());
            if(def==null) def = new LinkedList<>();
            for (Temp t : def) 
                vertices.add(t);
        }
        
        for (Temp t : vertices) {
            adj.put(t, new LinkedList<Temp>());
            dg.put(t, 0);
        }

        // Add edges
        for(Node node:nodes){
            assem.Instr instr = fg.mp.get(node);
            List<Temp> uses = TempList.toList(instr.use());
            if(uses==null) uses = new LinkedList<>();
            List<Temp> defs = TempList.toList(instr.def());
            if(defs==null) defs = new LinkedList<>();
            List<Temp> liveOut = TempList.toList(lv.liveOut(node));
            if(liveOut==null) liveOut = new LinkedList<>();
            Temp avoid = null;

            if(fg.isMove(node)){
                avoid = uses.get(0);
            }

            for(Temp def : defs){
                for(Temp out : liveOut){
                    if(out.equals(def) || out.equals(avoid))
                        continue;
                    addEdge(def, out);
                }
            }
        }
    }
}