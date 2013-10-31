package fruit.g6;

import java.util.Arrays;

public class Player extends fruit.sim.Player {
  //Book-keeping
  public final int NFRUIT = 12;
  int nplayers;
  int bowlSize;
  int[] mPreferences = new int[NFRUIT];
  double[] mExpDistrib = new double[NFRUIT];
  
  //State & property keepers
  boolean firstInvocation;
  int timesCanPass;
  int timesCanPassInitial;
  int passNumber;
  int stoppingThreshold;
  double deviationThreshold;
  
  //Parameter tuning
  boolean replaceInitialDistribution=false;
  boolean useStopping=true;
  boolean debug=true;
  boolean useLogisticFunctionDeviationFraction=true;
  boolean laterBowlMoreImp=true; //still not using this
  boolean takeReallyGoodBowl=true;
  
  public void init(int nplayers, int[] pref) {
	  this.nplayers=nplayers;
	  mPreferences=pref;
	  Arrays.fill(mExpDistrib, 1.0/NFRUIT);
	  roundInit(0);
  }
  
  public void roundInit(int round)
  {
	  if(debug)
		  print("Custom Initializing...");
	  firstInvocation=true;
	  passNumber=1;
	  timesCanPass=nplayers-1-getIndex();
	  if(round==1)
	  {
		  timesCanPass=nplayers-timesCanPass-1;
	  }
	  timesCanPassInitial=timesCanPass;
	  stoppingThreshold=(int)Math.floor(timesCanPass/Math.E);
  }

  public boolean pass(int[] bowl, int bowlId, int round,
                      boolean canPick,
                      boolean musTake) {
	  
   bowlSize=sumOfArray(bowl);
	  
   if (musTake || !canPick) {
     modifyDistribution(bowl);
     roundInit(1);
     return true;
   }
   
   double expectedScore=getExpectedScore();
   double bowlScore=getBowlScore(bowl);
   double deviation=getDeviation();
   
   
   
   double meanPlus;
   if(useLogisticFunctionDeviationFraction) //Chop off using logistic function 
   {
	   meanPlus=calculateDeviationThreshold()*scaledSigmoid(timesCanPass*1.0/timesCanPassInitial);
   }
   else //Linearly chop off
   {
	   meanPlus=deviation*timesCanPass/(timesCanPassInitial-1);
   }
   
   if(debug)
   {
	   print(String.format("timesCanPass %d timesCanPassInitial %d expected %f deviation %f deviationfraction %f", timesCanPass, timesCanPassInitial, expectedScore, deviation,calculateDeviationThreshold()));
	   print(String.format("Old expected=%f // bowlscore=%f ", (expectedScore+deviation*timesCanPass/(timesCanPassInitial-1)), bowlScore));
	   print(String.format("New expected=%f // bowlscore=%f ", (expectedScore+calculateDeviationThreshold()*scaledSigmoid(1.0*timesCanPass/timesCanPassInitial)), bowlScore));
	   print(String.format("stoppingthreshold=%d // passnumber=%d ", stoppingThreshold, passNumber));
   }
   
   boolean take = ((expectedScore+meanPlus) <= bowlScore || (takeReallyGoodBowl && bowlScore>=9*NFRUIT));
   modifyDistribution(bowl);
   
   
   if(useStopping && passNumber<=stoppingThreshold)
   {
	   take=false;
   }
   
   if(!take)
 	  timesCanPass--;
   else
 	  roundInit(1);
   
   passNumber++;
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
  private void modifyDistribution(int[] newBowl) {
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
  
  public double calculateDeviationThreshold()
  {
	 return getDeviation() * scaledSigmoid( 1.0*timesCanPass/ (nplayers-1) );
  }
  
  
  /* *
   * Sigmoid function that takes in value from 0 to 1, and outputs values from 0 to 1 
   * */
  public double scaledSigmoid(double x)
	{
		return 1/(1+Math.pow(Math.E,6-x*12));
	}
}
