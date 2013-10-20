package fruit.sim;

public abstract class Player
{
    int id;

    // your position
    int index;
    
    public int getIndex() {
        return index;
    }

    // Override
    public abstract void init(int nplayers, int[] pref);

    // Override
    // ask the player if he wants the current bowl
    public abstract boolean pass(int[] bowl, 
                                 int bowlId, int round,
                                 boolean canPick,   // the player can choose the bowl
                                 boolean mustTake); // the player must take the bowl
}
