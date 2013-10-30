package fruit.g3;

import java.util.*;

public class Player extends fruit.sim.Player
{
    public void init(int nplayers, int[] pref) {
		this.preferences = pref.clone();
		this.id = this.getIndex();
		this.players = nplayers;
		this.maxBowls[0] = this.players - this.id;
		this.maxBowls[1] = this.id + 1;
		System.out.println("Player ID: " + this.id);
		System.out.println("Max bowls can be seen: Round 0: " + this.maxBowls[0]);
		System.out.println("Max bowls can be seen: Round 1: " + this.maxBowls[1]);
		this.start[0] = 0;
		this.start[1] = maxBowls[0] - 1;
		this.len[0] = 0;
		this.len[1] = 1;
		if (this.maxBowls[0] < 4) this.strategy = 0;
		else this.strategy = 1;
		for (int i = 0; i < 12; i++) {
			this.fruits[i] = 1;
		}

    }

    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
		
		int bowlScore = 0;
		double expectedScore = 0;
		while (strategy == 0 || (strategy == 1 && round == 0)) {

			for (int i = 0; i < 12; i++) {
				fruits[i] += (double)(bowl[i])/2;
				bowlScore = bowlScore + (bowl[i]*preferences[i]);
				dist[i]+=bowl[i]/2.0;
			}
			System.out.println("Bowl Score: " + bowlScore);
			System.out.println("Round: "+round);

			bowlsSeen[round]++;

			if (maxBowls[round] == 3) {
				System.out.println("inside max bowls = 3");
				if (bowlsSeen[round] >= 2) {
					int maxScore = getMaxScore(scoresSeen, start[round], len[round]);
					scoresSeen.add(bowlScore);
					len[round]++;
					System.out.println("Max Score: "+maxScore);
					return maxScore <= bowlScore;
				}
				else
				{
					System.out.println("Want to pass... "+"true");
					scoresSeen.add(bowlScore);
					len[round]++;
					return false;
				}
			}
			System.out.println("n/e value: "+Math.floor((double)(maxBowls[round])/(double)(Math.E)));
		
			if (Math.floor((double)(maxBowls[round])/(double)(Math.E)) < bowlsSeen[round]) {
				System.out.println("inside general case");
				int maxScore = getMaxScore(scoresSeen, start[round], len[round]);
				scoresSeen.add(bowlScore);
				len[round]++;
				System.out.println("Max Score: "+maxScore);
				return maxScore <= bowlScore;
			}
			else {
				System.out.println("else...");
				scoresSeen.add(bowlScore);
				len[round]++;
				System.out.println("Want to pass... "+"true");
				return false;
			}		
		}

		double sum=0, sumDist=0;
		for (int i = 0; i < bowl.length; i++) {
			dist[i]+=bowl[i]/2.0;
			bowlScore = bowlScore + (bowl[i]*preferences[i]);
			expectedScore = expectedScore + (dist[i]*preferences[i]);
			sumDist+=dist[i];
			sum += bowl[i];
		}
		double rat=sum/sumDist;
		expectedScore = expectedScore * rat;

		System.out.println("Strategy 1");
		return bowlScore > expectedScore;
    }
	
	private int getMaxScore(ArrayList<Integer> scoresSeen, int start, int len) {
		int max = 0;
		for (int i=start; i<len+start; i++) {
			if (max < scoresSeen.get(i)) {
				max = scoresSeen.get(i);
			}
		}
		return max;
	}
    

    private Random random = new Random();
    private int[] preferences = new int[12];
	private double[] fruits = new double[12];
	private int id = 0;
	private int[] bowlsSeen = new int [2];
	private int[] maxBowls = new int[2];
	private int[] start = new int[2];
	private int players = 0;
	private int strategy;
	private double[] dist = new double[12];
	private int[] len = new int[2];
	private ArrayList<Integer> scoresSeen = new ArrayList<Integer>();
}

