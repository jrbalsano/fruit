package fruit.g5;
import java.io.*;
     
public class Player extends fruit.sim.Player
{
    final int typesOfFruit = 12;

	int nplayers, index, choice = -1;
	int[] pref;
	int[][] choicelist;
    int bowlSize;

    int firstRoundBowlsSeen = 0;
    int secondRoundBowlsSeen = 0;
    int[][] firstRoundBowlHistory;
    int[][] secondRoundBowlHistory;

    boolean firstBowlReceived = false;

	boolean createLog = false; // Toggle while debugging
	PrintWriter outfile;

	public void init(int nplayers, int[] pref)
	{
		this.nplayers = nplayers;
		this.pref = pref;
		this.index = getIndex();
		choicelist = new int[nplayers + 1][pref.length];

        this.firstRoundBowlHistory = new int[nplayers - getIndex()][typesOfFruit];
        this.secondRoundBowlHistory = new int[getIndex() + 1][typesOfFruit];

		if(createLog) try {
			FileWriter fstream = new FileWriter("fruit/fruit.g5/log.txt", false);
			outfile = new PrintWriter(fstream);
			outfile.println("Players : " + Integer.toString(nplayers));
			outfile.println("Index   : " + Integer.toString(index));
			outfile.println("");
			outfile.flush();
		} catch (Exception e){ }
	}


    public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean musTake)
    {
        updateStats(bowl, round);
        // Use the stopping strategy in round one
        if (round == 0) {
            return stoppingStrategy(bowl, bowlId, round, canPick, musTake);
        }
        else
        {
            predictDistribution();
            double expectedScore = calculateExpectedScore();

            // Take a bowl greater than your expected score based on the distribution
            if (bowlScore(bowl) > expectedScore) {
                return true;
            }
        }

        return false;
    }

	public boolean stoppingStrategy(int[] bowl, int bowlId, int round, boolean canPick, boolean musTake)
	{
		int choicesleft, cutoff = 0, numfruits = 0;
		boolean pickflag = false;
		
		choice++;
		choicelist[choice] = bowl.clone();

		for (int i = 0; i < bowl.length; i++)
			numfruits = numfruits + bowl[i];
		
		if(round == 0)
		{
			choicesleft = nplayers - index - choice; 
			if((choice + 1) > Math.round((nplayers - index) / Math.E))
				pickflag = true;
		}
		
		else
		{
			choicesleft = nplayers + 1 - choice;
			if((choice + 1) > Math.round((nplayers + 1) / Math.E))
				pickflag = true;
		}
		
		for(int i = choice - 1, j = 0; i > -1 && j < Math.round(choicesleft / (Math.E - 1));  i--, j++)
		{
			if(bowlScore(choicelist[i]) > cutoff)
				cutoff = bowlScore(choicelist[i]);
		}
		
		if(createLog)
		{
			outfile.println("Round     : " + Integer.toString(round));
			outfile.println("Choice    : " + Integer.toString(choice));
			outfile.println("Remaining : " + Integer.toString(choicesleft));
			outfile.println("Cutoff    : " + Integer.toString(cutoff));
			outfile.println("Score     : " + Integer.toString(bowlScore(bowl)));
			outfile.println("Pickflag  : " + Boolean.toString(pickflag));
			outfile.println("");
			outfile.flush();
		}
		
		if((pickflag && bowlScore(bowl) > cutoff) || (bowlScore(bowl) > (int) (9.0 * numfruits)))
			return true;

		return false;
	}

    /**
     * Count the number of each fruit and put it as a fraction over the total number of fruit seen.
     * This is the predicted distribution
     * @return An array of probabilities of each fruit
     */
    public double[] predictDistribution() {
        int[] fruitCounts = new int[12];
        int totalFruit = 0;
        double[] fruitProbabilities = new double[12];

        for (int i = 0; i < firstRoundBowlHistory.length; i++) {
            for (int j = 0; j < firstRoundBowlHistory[i].length; j++) {
                fruitCounts[j] += firstRoundBowlHistory[i][j];
            }
        }

        for (int i = 0; i < secondRoundBowlHistory.length; i++) {
            for (int j = 0; j < secondRoundBowlHistory[i].length; j++) {
                fruitCounts[j] += secondRoundBowlHistory[i][j];
            }
        }

        for (int i: fruitCounts) {
            totalFruit += i;
        }

        for (int i = 0; i < fruitCounts.length; i++) {
            fruitProbabilities[i] = (double) fruitCounts[i] / totalFruit;
        }

        return fruitProbabilities;
    }

    /**
     * Using the predicted distribution, calculated your expected score based on your preferences
     * @return The expected score
     */
    public double calculateExpectedScore() {
        double score = 0;
        double[] fruitProbabilities = predictDistribution();
        for (int i = 0; i < typesOfFruit; i++) {
            score += fruitProbabilities[i] * bowlSize * pref[i];
        }
        return score;
    }

    /**
     * Keeps track of all the bowls you've seen so far
     * @param bowl A fruit bowl
     * @param round The round number
     */
    public void updateStats(int[] bowl, int round)
    {
        // Once you receive the first bowl, calculate the size of a bowl
        if (!firstBowlReceived) {
            for (int i = 0; i < bowl.length; i++) {
                this.bowlSize += bowl[i];
            }
            firstBowlReceived = true;
        }

        if (round == 0) {
            firstRoundBowlHistory[firstRoundBowlsSeen] = bowl;
            firstRoundBowlsSeen++;
        } else if (round == 1) {
            secondRoundBowlHistory[secondRoundBowlsSeen] = bowl;
            secondRoundBowlsSeen++;
        }
    }


    /**
     * Calculates the score of a bowl
     * @param bowl A fruit bowl
     * @return The score of the bowl
     */
	public int bowlScore(int[] bowl)
	{
		int score = 0;
		for (int i = 0; i < bowl.length; i++)
		{
			score += pref[i] * bowl[i];
		}
		return score;
	}
}

