package fruit.g7;

import java.util.*;

public class Player extends fruit.sim.Player
{
    int[] preference;
    int n_players;
    int n_bowls;
	int bowlSize;
    private int[][] fruitHistory;
	private int[] originalDistribution;
	private int[] currentDistribution;
	private Random random = new Random();

    // Since there are 12 Fruits
    private static int NUM_FRUITS = 12;
   
    public void init(int nplayers, int[] pref) {
	    preference = pref;
	    n_players = nplayers;
	    n_bowls = nplayers*2; // Since there are 2 rounds
		
		originalDistribution = new int[NUM_FRUITS];
		currentDistribution = new int[NUM_FRUITS];
	    fruitHistory = new int[n_bowls][NUM_FRUITS];
	    for (int i=0 ; i<n_bowls ; i++){
		    for (int j=0 ; j<NUM_FRUITS ; j++){
			    fruitHistory[i][j] = 0;
		    }
	    }
    }
    
    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
		double expected;
	
		bowlSize = 0;
		for(int i=0; i<bowl.length; i++)
			bowlSize += bowl[i];
	
		estimateDistribution();
		fruitHistory = history(bowl, bowlId, round);
		expected = expectedValue(bowl, bowlId, round);
	
//		for (int i=0 ; i<preference.length ; i++)
//			System.out.print(preference[i] + ", ");

		if (musTake)
			return true;

		if (canPick && score(bowl, bowlId, round) >= expected)
			return true;
		else
		    return false;
    }

    private int[][] history(int[] bowl, int bowlId, int round){
	    for (int i=0 ; i<bowl.length ; i++){
		    fruitHistory[round*n_players + bowlId][i] = bowl[i];
	    }
    
	    return fruitHistory;
    }

    private int score(int[] bowl, int bowlId, int round){
	    int score = 0;
	    for (int i=0 ; i<bowl.length ; i++){
		    score += preference[i] * bowl[i];
	    }
	    System.out.println("Score: " + score);
	    return score;
    }

	private double expectedValue(int[] bowl, int bowlId, int round){
	    int bowlSize = 0;
	    for(int i=0; i<bowl.length; i++)
	    	bowlSize += bowl[i];

		double avgFruitScore = 0;
		int totFruitsInDistribution = 0;
		for(int i=0; i<NUM_FRUITS; i++){
			if(currentDistribution[i] > 0){
				totFruitsInDistribution += currentDistribution[i];
				avgFruitScore += preference[i] * currentDistribution[i];
			}
		}

		//if for some reason totFruitsInDistrubtion == 0
		//assume uniform
		if(avgFruitScore == 0)
			avgFruitScore = 6.5;
		else
			avgFruitScore /= totFruitsInDistribution;

	    double expected = bowlSize * avgFruitScore;
	    System.out.println("Exp: " + expected);
	    return expected;
    }

	private void estimateDistribution(){
		int totalSelections = 0;
		
		//Number of times each fruit category has been selected for distribution
		int[] selectedFruits = new int[NUM_FRUITS];
		
		//Number of fruit of each category has been distributed
		int[] distributedFruits = new int[NUM_FRUITS];
	
		for(int i = 0;i<fruitHistory.length;i++){
			for(int j = 0;j<fruitHistory[i].length;j++){
				//System.out.println("fruitHistory["+i+"]["+j+"]="+fruitHistory[i][j]);
				if(fruitHistory[i][j]!=0){
					totalSelections++;
					selectedFruits[j]++;
					distributedFruits[j] += fruitHistory[i][j];
				}
			}
		}
		
//		System.out.println("Bowl size:"+bowlSize);
//		System.out.println("TotalSelections:"+totalSelections);
		
//		for(int i = 0;i<selectedFruits.length;i++){
//			System.out.println("selectedFruits[i]:"+selectedFruits[i]);
//		}
		
		for(int i = 0;i<distributedFruits.length;i++){
			
			if(totalSelections==0)
				originalDistribution[i] = 0;
			else
				//For each fruit determine the percentage of chosen fruits
				//which are this fruit and multiply by the total number 
				//of fruit in the bowl being selected from
				originalDistribution[i] = (int) Math.round(n_players*bowlSize*selectedFruits[i]/totalSelections);
				
//			System.out.println("Estimate of original distribution of fruit " + i + " = " + originalDistribution[i]);
			
			//Estimate the current distribution by substracting the distributed fruits from the estimated original distribution
			currentDistribution[i] = originalDistribution[i] - distributedFruits[i];
//			System.out.println("Estimate of current distribution of fruit " + i + " = " + currentDistribution[i]);
		}
		for(int i=0;i<distributedFruits.length;i++)
			System.out.println("curr Dist: "+currentDistribution[i]);
	}
}
