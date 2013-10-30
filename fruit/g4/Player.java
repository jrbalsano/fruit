package fruit.g4;

import java.util.*;

public class Player extends fruit.sim.Player
{
  private float[] prefs;
  private float[] platter;
  private float numFruits = 0;
  private float bowlsRemaining;
  private float totalNumBowls;
  private int numPlayers;

  private MLE mle;


  public void init(int nplayers, int[] pref) {
    numPlayers = nplayers;
    prefs = Vectors.castToFloatArray(pref);
    platter = new float[pref.length];
    bowlsRemaining = (float)(nplayers - getIndex());
    totalNumBowls = bowlsRemaining;
    System.out.println(getIndex());
  }

  public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean mustTake) {
    // SETUP
    float[] currentBowl = Vectors.castToFloatArray(bowl);
    numFruits = Vectors.sum(currentBowl);
    if (!canPick){
      return false;
    }

    System.out.println("Number of bowls that will pass: " + totalNumBowls);
    System.out.println("Number of bowls remaining: " + bowlsRemaining);

    // Initialize the histogram now that we know how many fruit come in a bowl
    if (mle == null){
      mle = new MLE((int) numFruits, numPlayers);
    }
    mle.addObservation(currentBowl);

    // calculate score for the bowl the we get
    float score = score(currentBowl);

    // get MLE and score it
    float[] uniformBowl = new float[currentBowl.length];
    for (int i = 0 ; i < bowl.length; i++){
      uniformBowl[i] = numFruits / bowl.length;
    }
    float uniformScore = score(uniformBowl);

    System.out.println("Uniform Score: " + uniformScore);
    System.out.println("MLE Score: " + score(mle.bowl(round == 0)));
    System.out.println("Score: " + score);
    bowlsRemaining--;
    return shouldTakeBasedOnScore(score, score(mle.bowl(round == 0)));
  }

  private boolean shouldTakeBasedOnScore(float currentScore, float mle){
    // based on number of bowls remaining to pass you, decide if you should take
    if (currentScore < mle) return false;

    float diff = maxScore() - mle;
    // TODO: base off of nplayers as well
    return currentScore > (0.5f * diff * ((totalNumBowls - 1) / bowlsRemaining)) + mle;
  }

  private float maxScore(){
    return numFruits * 12;
  }

  private float score(float[] bowl){
    return Vectors.dot(bowl, prefs);
  }

  private Random random = new Random();
}
