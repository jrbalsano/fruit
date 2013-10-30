package fruit.g2;

import java.util.*;

public class Stat {
    private LinkedList<int[]> history;
    private int nplayers;
    private int nkindfruits;
    private int nfruits;
    private int[] pref;
    private int nround;
    
    public Stat(int nplayers, int[] pref) {
        history = new LinkedList<int[]>();
        this.nplayers = nplayers;
        this.pref = pref.clone();
        nkindfruits = pref.length;
        nfruits = 0;
        nround = 0;
    }

    public int getNFruits(){
	return nfruits;
    }

    public void add(int[] bowl) {
        assert(nkindfruits == bowl.length);
        if (nfruits == 0)
            nfruits = sum(bowl);
        assert(nfruits == sum(bowl));
        nround++;
        history.add(bowl);
        assert(nround == history.size());
    }

    public int score(int round) {
        assert(round < nround);
        assert(round >= 0);
        int[] bowl = history.get(round);
        return dot(pref, bowl);
    }

    public double average() {
        // no data - guess an average
        if (nround == 0)
            return nfruits * sum(pref)/nkindfruits;
        double sum = 0;
        for (int i = 0; i < nround; i++)
            sum += score(i);
        return sum/nround;
    }


    public double stdev() {
        double avg = average();
        int sum = 0;
        for (int i = 0; i < nround; i++) {
            double diff = avg - score(i);
            sum += Math.pow(diff, 2.0);
        }
        return Math.pow((sum/nround), 0.5);

    }

    private int sum(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    private int dot(int[] a, int[] b) {
        assert(a.length == b.length);
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
    
}
