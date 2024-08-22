package translate;
import tree.*;
import frame.*;
//import java.util.List;
public class ProcFrag extends Frag
{
    public Stm body;
    public Frame frame;
    
    public ProcFrag(Stm body, Frame frame)
    {
        this.body  = body;
        this.frame = frame;
    }
}