package fruit.g7;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
	// Uniform Distribution
	public int[] generate2(int nplayers, int bowlsize) {
		int nfruits = nplayers * bowlsize;

		int[] dist = new int[12];
		int unit = nfruits / 12;

		dist[0] = nfruits - unit * 11;
		for (int i = 1; i < 12; i++)
			dist[i] = unit;

		return dist;
	}

	// Heavily biased monotonically decreasing
	public int[] generate(int nplayers, int bowlsize) {
		int nfruits = nplayers * bowlsize;
		int[] dist = new int[12];
		
		int bin1 = nfruits * 3 / 6;
		int bin2 = nfruits * 2 / 6;
		int bin3 = nfruits - bin2 - bin1;

		int sum = 0;
		for(int i=1; i<4; i++){
			dist[i] = bin1/4;
			sum+= dist[i];
		}
		dist[0] = bin1 - sum;

		sum = 0;
		for(int i=5; i<8; i++){
			dist[i] = bin2/4;
			sum+= dist[i];
		}
		dist[4] = bin2 - sum;
		
		sum = 0;
		for(int i=9; i<12; i++){
			dist[i] = bin3/4;
			sum+= dist[i];
		}
		dist[8] = bin3 - sum;

		float[] dist_float = new float[12];
		for (int i=0; i<12 ; i++)
			dist2[i] = dist[i]/12.0;

		return dist2;
	}
}