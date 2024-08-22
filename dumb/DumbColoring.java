package dumb;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import temp.Temp;
import temp.TempList;
import temp.TempMap;

public class DumbColoring implements TempMap{
    HashMap<Temp,Temp> toFrameTemp;
    TempMap frameColors;
    TempList spill;

    public DumbColoring(DumbInterferenceGraph ig, Temp[] registers, TempMap frameColors) {
        spill = null;
        toFrameTemp = new HashMap<>();
        this.frameColors = frameColors;
        
        for (Temp u : ig.vertices) {
            if(frameColors.tempMap(u)!=null){
                toFrameTemp.put(u, u);
            }
        }

        for (Temp u : ig.vertices) {
            if(toFrameTemp.get(u)!=null) continue;
            HashSet<Temp> usedColors = new HashSet<Temp>();
            for(Temp v : ig.adj.get(u)){
                usedColors.add(toFrameTemp.get(v));
            }
            Temp color = null;
            for(int i=0;i<registers.length;i++){
                if(usedColors.contains(registers[i])==false){
                    color = registers[i];
                    break;
                }
            }
            toFrameTemp.put(u, color);
            if(color==null)
                spill = new TempList(u, spill);
        }
    }   

    public String tempMap(Temp t) {
        Temp x = toFrameTemp.get(t);
        if(x!=null)
            return frameColors.tempMap(x);
        return null;
    }
    
}
