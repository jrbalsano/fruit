package fruit.g6;

public class Player extends fruit.sim.Player {
  public final int NFRUIT = 12;
  int[] mExpDistrib = new int[NFRUIT];
  int mPreferences = new int[NFRUIT];
  
  public void init(int nplayers, int[] pref) {

  }

  public boolean pass(int[] bowl, int bowlId, int round,
                      boolean canPick,
                      boolean musTake) {
   if (musTake || !canPick) {
     generateDistribution(newBowl);
     return true;
   }
   
   boolean take = getExpectedScore() <= getBowlScore(bowl);
   generateDistribution(newBowl);
   return take;
  }

  
  private int getExpectedScore() {
    int expected = 0;
    for (int i = 0; i < NFRUIT; i++) {
      expected += mExpDistrib[i] * mPreferences[i];
    }
    return expected;
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
    
  }
  
}
