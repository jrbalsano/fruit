package fruit.g2;

import java.util.*;

public class Player extends fruit.sim.Player
{
    private Stat stat;
    private int nplayers;
    private int[] pref;
    // number of times that a bowl passed to the player
    // at the end of first round npassed+getindex() = nplayers
    private int npassed;
    private int round;
    // Housekeeping, useless in functionality
    private boolean picked;

    public void init(int nplayers, int[] pref) {
        stat = new Stat(nplayers, pref);
        this.nplayers = nplayers;
        this.pref = pref.clone();
        npassed = 0;
        round = 0;
        picked  = false;
    }

    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean mustTake) {
        npassed++;
        stat.add(bowl);
	System.out.println("choice left: " + choiceLeft() + " can pick " + canPick);
        if (choiceLeft() == 0 && this.round == 0) {
            // beginning of second round
            assert(picked);
            picked = false;
            this.round = 1;
        }
        assert(round == this.round);
        assert(picked != canPick);
        // already picked
        if (picked)
            return false;
        // must take
        if (choiceLeft() == 1) {
            if (!picked) {
                assert(mustTake);
                picked = true;
                return true;
            }
        }
        // otherwise look for avg + std
        else {
	    double coeff = getCoeff(choiceLeft());
	    System.out.println("Score to take: " + (stat.average() + coeff*adjustedStdDev(stat.stdev())));
            if (stat.score(npassed-1) < stat.average() + coeff*adjustedStdDev(stat.stdev()))
                return false;
            else {
                picked = true;
                return true;
            }
        }
        return false;
    }
    
    public double getCoeff(int choiceLeft){

	if (choiceLeft < 2)
	   return 0.0;
        else{
	   System.out.println("coefficient chosen: " + (choiceLeft/4.0 - .5));
           return choiceLeft/4.0-.5;
	}
    }

    public double adjustedStdDev(double stdev){

     int numfruits = stat.getNFruits();
     double influence = Math.pow(.5, (double)npassed-1);
     return influence*numfruits+(1-influence)*stdev;

    }

    private int choiceLeft() {
        if (round == 0)
            return nplayers - getIndex()- npassed+1;
        else
            return nplayers - npassed+2;
    }
}
