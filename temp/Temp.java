package temp;

public class Temp {
  private static int count;
  private int num;
  public int getNum() {return num;}

  public String toString() {
    return "t" + num;
  }

  public Temp() {
    num = count++;
  }

  public int hashCode() { 
        return num; 
    }

    public boolean spillTemp = false;
}