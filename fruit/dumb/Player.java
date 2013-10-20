package fruit.dumb;

import java.util.*;

public class Player extends fruit.sim.Player
{
    public void init(int nplayers, int[] pref) {
    }

    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
        return random.nextDouble() > 0.5;
    }
    

    private Random random = new Random();
}
