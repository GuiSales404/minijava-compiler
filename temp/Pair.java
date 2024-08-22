package temp;

public class Pair {
    Temp t1, t2;

    public Pair(){

    }

    public Pair(Temp t1, Temp t2){
        this.t1 = t1;
        this.t2 = t2;
    }

    public int hashCode() { 
        if(t1.hashCode() < t2.hashCode()){
            return (t1.hashCode()<<20) + t2.hashCode();
        }
        return (t2.hashCode()<<20) + t1.hashCode();
    }
}
