package fruit.g6;

import java.util.Arrays;

public class Player extends fruit.sim.Player {
  public final int NFRUIT = 12;
  int nplayers;
  double[] mExpDistrib = new double[NFRUIT];
  int[] mPreferences = new int[NFRUIT];
  boolean replaceInitialDistribution=false;
  boolean firstInvocation;
  int timesCanPass;
  int timesCanPassInitial;
  int bowlSize;
  int passNumber;
  boolean debug=true;
  
  public void init(int nplayers, int[] pref) {
	  this.nplayers=nplayers;
	  mPreferences=pref;
	  Arrays.fill(mExpDistrib, 1.0/NFRUIT);
	  roundInit();
  }
  
  public void roundInit()
  {
	  firstInvocation=true;
	  passNumber=1;
	  timesCanPass=nplayers-1-getIndex();
	  timesCanPassInitial=timesCanPass;
  }

  public boolean pass(int[] bowl, int bowlId, int round,
                      boolean canPick,
                      boolean musTake) {
	  
   bowlSize=sumOfArray(bowl);
	  
   if (musTake || !canPick) {
     generateDistribution(bowl);
     roundInit();
     return true;
   }
   
   double expectedScore=getExpectedScore();
   double bowlScore=getBowlScore(bowl);
   double deviation=getDeviation();
   
   if(debug)
   {
	   print(String.format("timesCanPass %d timesCanPassInitial %d expected %f deviation %f", timesCanPass, timesCanPassInitial, expectedScore, deviation));
	   print(String.format("expected=%f // bowlscore=%f ", (expectedScore+deviation*timesCanPass/(timesCanPassInitial-1)), bowlScore));
   }
   
   boolean take = ((expectedScore+deviation*timesCanPass/(timesCanPassInitial-1)) <= bowlScore);
   generateDistribution(bowl);
   
   passNumber++;
   
   if(!take)
	   timesCanPass--;
   else
	   roundInit();
   return take;
  }

  public double getDeviation()
  {
	  double deviation=0;
	  
	  for (int i = 0; i < NFRUIT; i++) 
	  {
		  deviation += (mExpDistrib[i] * mPreferences[i] * mPreferences[i]);
	  }
	  
	  return Math.sqrt(deviation);
  }
  
  private double getExpectedScore() {
    double expected = 0;
    
    for (int i = 0; i < NFRUIT; i++) {
      expected += (mExpDistrib[i] * mPreferences[i]);
    }
    expected*=bowlSize;
    
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
	  if(firstInvocation)
	  {
		  firstInvocation=false;
		  if(replaceInitialDistribution)
		  {
			  Arrays.fill(mExpDistrib, 0);
		  }
	  }
	  
	  for(int i=0;i<newBowl.length;i++)
	  {
		  mExpDistrib[i]=(mExpDistrib[i]*mExpDistrib.length*passNumber + newBowl[i]);
		  mExpDistrib[i]/=1.0*(mExpDistrib.length*passNumber+sumOfArray(newBowl));
	  }
	  
	  
  }
  
  /*
   * Returns sum of elements in a int array
   */
  private int sumOfArray(int[] arr)
  {
	  int sum=0;
	  for(double a: arr)
	  {
		  sum+=a;
	  }
	  return sum;
  }
  
  /*
   * Returns sum of elements in a double array
   */
  private double sumOfArray(double[] arr)
  {
	  double sum=0;
	  for(double a: arr)
	  {
		  sum+=a;
	  }
	  return sum;
  }
  
  public void print(String s)
  {
	  System.out.println(s);
  }
}
