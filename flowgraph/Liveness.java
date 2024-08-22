package flowgraph;
import graph.Node;
import graph.NodeList;
import syntaxtree.True;
import temp.Temp;
import temp.TempList;

import java.util.BitSet;
import java.util.HashMap;


public class Liveness {
    AssemFlowGraph g;
    HashMap<Integer,Temp> aux;
    public HashMap<Node,BitSet> in, out, def, use;
    

    public Liveness(AssemFlowGraph g){
        this.g = g;
        aux = new HashMap<Integer,Temp>();  
        in = new HashMap<Node,BitSet>();
        out = new HashMap<Node,BitSet>();
        def = new HashMap<Node,BitSet>();
        use = new HashMap<Node,BitSet>();
        solve();
    }

    public void solve(){
        NodeList nodes = g.nodes();

        for(;nodes!=null;nodes = nodes.tail){
            in.put(nodes.head, new BitSet());
            out.put(nodes.head, new BitSet());
            
            BitSet stDef = new BitSet();
            temp.TempList defs = g.def(nodes.head);
            for(;defs!=null;defs = defs.tail){
                aux.put(defs.head.getNum(), defs.head);
                stDef.set(defs.head.getNum());
            }
            def.put(nodes.head, stDef);

            BitSet stUse = new BitSet();
            temp.TempList uses = g.use(nodes.head);
            for(;uses!=null;uses = uses.tail){
                aux.put(uses.head.getNum(), uses.head);
                stUse.set(uses.head.getNum());
            }
            use.put(nodes.head, stUse);
        }

        int maxIter = 100;
        boolean changed;
        for(int iter=0;iter<maxIter;iter++){
            changed = false;

            nodes = g.nodes();
            for(;nodes!=null;nodes=nodes.tail){
                BitSet newIn = (BitSet)out.get(nodes.head).clone();
                newIn.andNot(def.get(nodes.head));
                newIn.or(use.get(nodes.head));
                if(newIn.equals(in.get(nodes.head))==false){
                    changed = true;
                    in.put(nodes.head, newIn);
                }

                BitSet newOut = new BitSet();
                NodeList succs = nodes.head.succ();
                for(;succs!=null;succs=succs.tail){
                    newOut.or(in.get(succs.head));
                }
                if(newOut.equals(out.get(nodes.head))==false){
                    changed = true;
                    out.put(nodes.head, newOut);
                }
            }

            if(changed==false){
                break;
            }
        }
    }

    public TempList liveOut(Node u){
        TempList answ = null;
        BitSet st = out.get(u);
        for(int i=st.length()-1;i>=0;i--){
            if(st.get(i)){
                answ = new TempList(aux.get(i), answ);
            }
        }

        return answ;
    }
}
