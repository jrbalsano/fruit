package fruit.sim;

public class UniformFruitGenerator implements FruitGenerator
{
    public int[] generate(int nfruits) {
        int[] dist = new int[12];
        int unit = nfruits / 12;
        
        dist[0] = nfruits - unit * 11;
        for (int i = 1; i < 12; i++)
            dist[i] = unit;
        
        return dist;
    }
}
