package fruit.g4;

public class FruitGenerator implements fruit.sim.FruitGenerator
{
  private int[] dist;

    public int[] generate(int nplayers, int bowlsize) {
        int nfruits = nplayers * bowlsize;

        dist = new int[12];
        int distChoice = 2;

        if(distChoice == 1)
          uniform(nfruits);
        else if(distChoice == 2)
          halfFruits(nfruits);

        return dist;
    }

    private void uniform(int nfruits) {
        int unit = nfruits / 12;

        dist[0] = nfruits - unit * 11;
        for (int i = 1; i < 12; i++)
            dist[i] = unit;
    }

    private void halfFruits(int nfruits) {
      int r;
      int subset[] = new int[6];
      boolean filled = false;

      for(int i=0; i < 6; i++) {
        boolean unique;
        do {
          unique = true;
          r = (int)(Math.random()*12);
          for(int j=0; j < i; j++) {
            if(subset[j] == r)
              unique = false;
          }
        }while(!unique);
        subset[i] = r;
      }

      while(nfruits > 0) {
        r = (int)(Math.random()*subset.length);
        dist[subset[r]]++ ;
        nfruits--;
      }
    }
}
