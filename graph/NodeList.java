package graph;

import java.util.LinkedList;
import java.util.List;

public class NodeList {
  public int size;
  public Node head;
  public NodeList tail;
  public NodeList(Node h, NodeList t) {
    size = 1;
    head=h; 
    tail=t;
    if(tail!=null)
      size += tail.size;
  }

  public static List<Node> toList(NodeList x){
    if(x==null) return null;
    List<Node> answ = new LinkedList<Node>();
    for(;x!=null;x = x.tail)
      answ.add(x.head);
    return answ;
  }
}



