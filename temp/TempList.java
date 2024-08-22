package temp;

import java.util.LinkedList;
import java.util.List;

public class TempList {
   public Temp head;
   public TempList tail;

   public TempList(Temp h, TempList t) {
      head = h;
      tail = t;
   }

   public static List<Temp> toList(TempList x){
        if(x==null) return null;
        List<Temp> answ = new LinkedList<Temp>();
        for(;x!=null;x = x.tail)
            answ.add(x.head);
        return answ;
    }   
}
