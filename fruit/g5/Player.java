package fruit.g5;
import java.util.*;
import java.io.*;    
     
public class Player extends fruit.sim.Player
{

	int nplayers, index, choice = -1;
	int[] pref;
	int[][] choicelist;
	
	boolean createLog = false; // Toggle while debugging
	PrintWriter outfile;

	
	public void init(int nplayers, int[] pref)
	{
		this.nplayers = nplayers;
		this.pref = pref;
		this.index = getIndex();
		choicelist = new int[nplayers + 1][pref.length];
		
		if(createLog) try {
			FileWriter fstream = new FileWriter("fruit/g5/log.txt", false);
			outfile = new PrintWriter(fstream);
			outfile.println("Players : " + Integer.toString(nplayers));
			outfile.println("Index   : " + Integer.toString(index));
			outfile.println("");
			outfile.flush();
		} catch (Exception e){ }
	}

	
	public boolean pass(int[] bowl, int bowlId, int round, boolean canPick, boolean musTake)
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

