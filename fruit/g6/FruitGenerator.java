package fruit.g6;

import java.util.Random;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        Random random = new Random();
        int nfruits = nplayers * bowlsize;  
        int[] dist = new int[12];
        int left1 = (int) Math.floor(0.75*nfruits);
        int left2 = nfruits - left1 + 1;
        for (int i = 0; i < 6; i++) {
            int cnt = random.nextInt(left1);
            dist[i] = cnt;
            left1 -= cnt;
        }
        for (int i = 6; i < 12; i++) {
            int cnt = random.nextInt(left2);
            dist[i] = cnt;
            left2 -= cnt;
        }
        return dist;
    }
}
