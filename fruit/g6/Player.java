package fruit.g6;

import java.util.Arrays;

public class Player extends fruit.sim.Player {
  public final int NFRUIT = 12;
  double[] mExpDistrib = new double[NFRUIT];
  int[] mPreferences = new int[NFRUIT];
  boolean replaceInitialDistribution=false;
  boolean firstInvocation;
  int passNumber;

  public void init(int nplayers, int[] pref) {

    firstInvocation=true;
    Arrays.fill(mExpDistrib, 1.0/NFRUIT);
    passNumber=1;

  }

  public boolean pass(int[] bowl, int bowlId, int round,
      boolean canPick,
      boolean musTake) {

    if (musTake || !canPick) {
      generateDistribution(bowl);
      return true;
    }

    boolean take = getExpectedScore() <= getBowlScore(bowl);
    generateDistribution(bowl);

    passNumber++;
    return take;
  }


  private int getExpectedScore() {
    double expected = 0;
    for (int i = 0; i < NFRUIT; i++) {
      expected += mExpDistrib[i] * mPreferences[i];
    }
    return (int) Math.floor(expected);
  }

  /* Returns score of current bowl */
  private int getBowlScore(int[] bowl) {
    int score = 0;
    for (int i = 0; i < NFRUIT; i++) {
      score += bowl[i] * mPreferences[i];
    }
    return score;
  }

  /**
   * Average the newBowl with the current expected distribution
   * @param newBowl
   */
  private void generateDistribution(int[] newBowl) {
    if(replaceInitialDistribution && firstInvocation)
    {
      firstInvocation=false;
      Arrays.fill(mExpDistrib, 0);
    }

    for(int i=0;i<newBowl.length;i++)
    {
      mExpDistrib[i]=(mExpDistrib[i]*mExpDistrib.length*passNumber + newBowl[i]);

      mExpDistrib[i]/=1.0*(mExpDistrib.length*passNumber+sumOfArray(newBowl));
    }

  }

  private double sumOfArray(int[] arr)
  {
    int sum=0;
    for(double a: arr)
    {
      sum+=a;
    }
    return sum;
  }

  private double sumOfArray(double[] arr)
  {
    double sum=0;
    for(double a: arr)
    {
      sum+=a;
    }
    return sum;
  }

}
