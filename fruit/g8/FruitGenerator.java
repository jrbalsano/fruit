package fruit.g8;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
    	//System.out.println("our own generator");
        int nfruits = nplayers * bowlsize;

        int[] dist = new int[12];
        int unit = nfruits / 2;
        
        dist[0] = unit;
        dist[11] = nfruits;
        for (int i = 1; i < 11; i++)
            dist[i] = dist[i-1]/2;
        for (int i=0; i<11; i++) {
        	dist[11]-=dist[i];
        }
        return dist;
    }
    
}
