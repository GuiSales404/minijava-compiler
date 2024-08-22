package flowgraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import graph.Node;
import temp.LabelList;
import temp.TempList;

public class AssemFlowGraph extends FlowGraph {
    public HashMap<Node,assem.Instr> mp;

    public AssemFlowGraph(){
        super();
        mp = new HashMap<Node,assem.Instr>();
    }

    public AssemFlowGraph(List<assem.Instr> instrlist){
        super();
        mp = new HashMap<Node,assem.Instr>();
        build(instrlist);
    }

    public void build(List<assem.Instr> instrlist){
        Node last = null;
        HashMap<temp.Label,Node> recover = new HashMap<temp.Label,Node>();

        // Loop creating Nodes
        Iterator<assem.Instr> it = instrlist.iterator();
        List<Node> nodes = new LinkedList<Node>();
        while(it.hasNext()){
            Node currNode = newNode();
            assem.Instr currInstr = it.next();
            // put on map
            mp.put(currNode,currInstr);
            // put on nodelist
            nodes.add(currNode);
            // if label then save node on recover
            if(currInstr instanceof assem.LABEL)
                recover.put(((assem.LABEL)currInstr).label, currNode);
        }
        
        // Loop creating edges
        Iterator<Node> nodesIt = nodes.iterator();
        it = instrlist.iterator();
        while(it.hasNext()){
            Node currNode = nodesIt.next();
            assem.Instr currInstr = it.next();

            // Add edges
            if(last!=null)
                addEdge(last, currNode);
            if(currInstr instanceof assem.OPER){
                temp.LabelList ls = (currInstr.jumps()).labels;
                for(;ls!=null;ls=ls.tail)
                    addEdge(currNode,recover.get(ls.head));
            }
            last = currNode;
        }
    }

    @Override
    public TempList def(Node node) {
        return mp.get(node).def();
    }

    @Override
    public TempList use(Node node) {
        return mp.get(node).use();
    }

    @Override
    public boolean isMove(Node node) {
        return (mp.get(node) instanceof assem.MOVE);
    }


}