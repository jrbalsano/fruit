package fruit.g5;

import java.util.Random;

// Normal Distribution

public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;
		int sum1 = 0, sum2 = 0;

        int[] dist = new int[12];
        Random r = new Random();

        for (int i = 0; i < nfruits; i++) {
            // mean 6.5 stdev 3
            int fruitToIncrease = (int) Math.round(r.nextGaussian() * 3 + 6.5);

            // if we get an invalid number, then try again
            if (fruitToIncrease > 11 || fruitToIncrease < 1) {
                i--;
                continue;
            }
            dist[fruitToIncrease] += 1;
        }

        return dist;
    }
}
