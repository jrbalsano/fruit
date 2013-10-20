package fruit.dumb;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;

        int[] dist = new int[12];
        int unit = nfruits / 12;
        
        dist[0] = nfruits - unit * 11;
        for (int i = 1; i < 12; i++)
            dist[i] = unit;
        
        return dist;
    }
}
