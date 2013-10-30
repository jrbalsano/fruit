package fruit.g3;

//Fruit generator: hit and miss
public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;

        int[] dist = new int[12];
        int unit = nfruits / 6;
        
        dist[0] = nfruits - unit * 5;
        for (int i = 2; i < 12; i += 2)
            dist[i] = unit;
        
        return dist;
    }
}
