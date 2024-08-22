package regalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import flowgraph.AssemFlowGraph;
import flowgraph.Liveness;
import graph.Graph;
import graph.Node;
import graph.NodeList;
import temp.Temp;
import temp.TempList;

public class InterferenceGraph extends Graph{
    AssemFlowGraph fg;
    Liveness lv;
    temp.TempMap tempMap;

    HashMap<Temp,Node> mp;
    HashMap<Node,Integer> degree;

    List<assem.MOVE> worklistMoves;
    public HashMap<temp.Temp,List<assem.MOVE>> MoveList;
    HashSet<temp.Pair> edges;

    public InterferenceGraph(AssemFlowGraph fg, temp.TempMap tempMap){
        this.fg = fg;
        mp = new HashMap<Temp,Node>();
        MoveList = new HashMap<temp.Temp,List<assem.MOVE>>();
        this.tempMap = tempMap;
        build();
    }

    void addToMoveList(Temp t, assem.MOVE mv){
        List<assem.MOVE> ls = MoveList.get(t);
        ls.add(mv);
    }

    public Node newNode(temp.Temp t){
        if(mp.get(t)==null){
            Node noh = super.newNode();
            mp.put(t,noh);
            degree.put(noh, 0);
            MoveList.put(t, new LinkedList<assem.MOVE>());
        }
        return mp.get(t);
    }

    public List<assem.MOVE> moves(){
        return worklistMoves;
    }

    public int spillCost(Node node) {
        return 1;
    }

    void build(){
        lv = new Liveness(fg);  
        worklistMoves = new LinkedList<assem.MOVE>();

        // Create nodes
        NodeList nodes = fg.nodes();
        for(;nodes!=null;nodes=nodes.tail){
            temp.TempList uses = fg.use(nodes.head);
            for(;uses!=null;uses=uses.tail){
                if(mp.get(uses.head)==null){
                    mp.put(uses.head, newNode());
                }
            }

            temp.TempList defs = fg.def(nodes.head);            
            for(;defs!=null;defs=defs.tail){
                if(mp.get(defs.head)==null){
                    mp.put(defs.head, newNode());
                }
            }
        }

        // Add edges
        nodes = fg.nodes();
        for(;nodes!=null;nodes=nodes.tail){
            Node u = nodes.head;
            if(fg.isMove(u)){
                Temp used = fg.use(u).head;
                Temp defined = fg.def(u).head;

                TempList out = lv.liveOut(u);
                for(;out!=null;out=out.tail){
                    if(out.head==defined || out.head==used) continue;
                    addEdge(defined, out.head);
                }

                worklistMoves.add((assem.MOVE)fg.mp.get(u));
                addToMoveList(defined,(assem.MOVE)fg.mp.get(u));
                addToMoveList(used,(assem.MOVE)fg.mp.get(u));
            }
            else{
                temp.TempList defs = fg.def(u);
                for(;defs!=null;defs=defs.tail){
                    TempList out = lv.liveOut(u);
                    for(;out!=null;out=out.tail){
                        if(out.head==defs.head) continue;
                        addEdge(defs.head, out.head);
                    }
                }
            }
        }
    }   

    public void addEdge(Temp from, Temp to){
        if(from!=to && edges.contains(new temp.Pair(from,to))==false){
            edges.add(new temp.Pair(from,to));
            
            if(tempMap.tempMap(from)==null){
                super.addEdge(mp.get(from),mp.get(to));
                Integer grau = degree.get(mp.get(from));
                if(grau==null) grau = 0;
                degree.put(mp.get(from),grau+1);
            }
            if(tempMap.tempMap(to)==null){
                super.addEdge(mp.get(to),mp.get(from));
                Integer grau = degree.get(mp.get(to));
                if(grau==null) grau = 0;
                degree.put(mp.get(to),grau+1);
            }
        }
    }
}
