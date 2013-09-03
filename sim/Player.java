package fruit.sim;

public interface Player
{
    public String name();
    public void init();

    // show the player what is the bowl in this turn
    public void tell(int[] bowl);
    
    // ask the player if it wants the bowl
    public boolean ask(int[] bowl);

}
