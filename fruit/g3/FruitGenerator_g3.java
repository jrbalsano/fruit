package fruit.g3;

import java.util.*;

public class FruitGenerator_g3 implements fruit.sim.FruitGenerator
{
    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;

        int[] dist = new int[12];
       
		int[] zeros = new int[2];
	    Random random = new Random();
		int fruitsLeft = nfruits;
		int maxval = random.nextInt(12);
		int count = 0;
		while (true) { 
			int temp = random.nextInt(12);
			if (count > 1) {
				break;
			}
		   	if (temp != maxval) {
				if (count == 1 && temp == zeros[0]) {
					continue;
				}
				zeros[count] = temp;
				count++;
			}
		}

		while (true) {
			double temp = random.nextDouble();
			if (temp > 0.2 && temp < 0.4) {
				dist[maxval] = (int)(fruitsLeft*temp);
				fruitsLeft = fruitsLeft - dist[maxval];
				break;
			}
		}

		dist[zeros[0]] = 0;	
		dist[zeros[1]] = 0;	
		
		count = 9;
		for (int i = 0; i < 12; i++) {
			if (i != maxval && i != zeros[0] && i != zeros[1]) {
				int temp = random.nextInt(fruitsLeft/count);
				while (temp == 0) {
					temp = random.nextInt(fruitsLeft/count);
				}
				dist[i] = temp;
				fruitsLeft = fruitsLeft - temp;
				count = count - 1;
			}
		}	

		System.out.println("Maximum value " + maxval + " distribution: " + dist[maxval]);
		while (true) { 
			int temp = random.nextInt(12);
		   	if (temp != maxval && temp != zeros[0] && temp != zeros[1]) {
				System.out.println("Initial " + temp + " distribution: " + dist[temp]);
				dist[temp] = dist[temp] + fruitsLeft;
				System.out.println("Final " + temp + " distribution: " + dist[temp]);
				break;
			}
		}
        
		/* int unit = nfruits / 12;
         dist[0] = nfruits - unit * 11;
         for (int i = 0; i < 12; i++)
            dist[i] = unit; */

	    /* Random random = new Random();
        for (int i = 0; i < 12; i++) {
			int val = random.nextInt(fruitsLeft);
			dist[i] = val;
			fruitsLeft = fruitsLeft - val;
		} */
        
        return dist;
    }
}
