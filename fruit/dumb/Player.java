package fruit.dumb;

import java.util.*;

public class Player extends fruit.sim.Player
{
    public void init(int nplayers, int[] pref) {
        System.out.println("my index: " + getIndex());
        
        System.out.println("my preferences:");
        System.out.println(Arrays.toString(pref));
    }

    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
        return random.nextDouble() > 0.5;
    }
    

    private Random random = new Random();
}
