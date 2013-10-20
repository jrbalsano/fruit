package fruit.sim;

import fruit.sim.FruitGenerator;
import java.util.Random;

public class RandomFruitGenerator implements FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        Random random = new Random();

        int nfruits = nplayers * bowlsize;
        int left = nfruits;

        int[] dist = new int[12];

        for (int i = 1; i < 11; i++) {
            int cnt = random.nextInt(left);
            dist[i] = cnt;
            left -= cnt;
        }
        dist[0] = left;

        return dist;
    }
}
