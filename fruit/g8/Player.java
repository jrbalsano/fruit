package fruit.g8;

import java.util.*;

public class Player extends fruit.sim.Player
{
	int nplayer;
	int[] pref;
	int[] record;
	int magic;
	int position;
	int magic_table[]={-1,0,0,1,1,2,2,2,3,3};
    public void init(int nplayers, int[] pref) {
    	this.nplayer=nplayers;
    	this.pref=pref;
    	this.position=this.getIndex();// position start from 0
    	this.record = new int[2*nplayer];
        if(nplayers-position<=9)
        	magic=magic_table[nplayers];
        else
        	magic=(int) Math.round(0.369*(nplayers-position) );
        
    }
    int max=0;
    int counter=0;
    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
    	counter++;
    	System.out.printf("\n counter is %d\n", counter);
    	record[counter-1]=score(bowl);
    	
    	if (musTake){
			return true;
		}
    	if (round==0) {
            return round0(bowl,bowlId,round,canPick,musTake);
        } else {
            return round1(bowl,bowlId,round,canPick,musTake);
        }
    	
    }
    
	private boolean round0(int[] bowl, int bowlId, int round,
            boolean canPick,
            boolean musTake) {
    	//System.out.printf("magic is %d", magic);
        if(counter<=magic){
        	System.out.println("we won't pick the bowl");
        	System.out.printf("the counter is %d\n", counter);
        	if (counter>2){
        		System.out.println("we are in counter>>2");
        		boolean grab=pickduringobservation(bowl, record);
        		if (grab)
        			return true;
       }
        	if (max<score(bowl)){
        		max=score(bowl);
        	}
        }else{
        	//System.out.println("we are in the picking round");
        	if(score(bowl)>=max){
        		return true;
        	}else{
        		return false;
        	}
        }
        return false;
	}
	
	private boolean round1(int[] bowl, int bowlId, int round, boolean canPick,
				boolean musTake) {
			return false;
		}

	private int score(int[] bowl){
    	int sum=0;
    	for(int i=0;i<12;i++){
    		sum+=pref[i]*bowl[i];
    	}
    	return sum;
    }

	private boolean pickduringobservation(int[] bowl, int[] record) {
		System.out.println("we are in the pickduring observation");
		System.out.printf("\n the score for this bow is %d\n", score(bowl));
		if (score(bowl)>average(record)*1.5 && score(bowl)>=max)
			return true;
		else
			return false;
	}
	
	private double average(int[] record){
		int sum=0;
		int i;
		double avg;
		int ct = 0;
		for (i=0;i<record.length;i++){
			sum = sum+record[i];
			System.out.printf("\n the %d th record is %d\n",i, record[i]);
			if (record[i]!=0)
				ct=i;
		}
		avg = sum/(ct+1);
		System.out.printf("\n the avg score is %f\n", avg);
		return avg;
	}
    private Random random = new Random();
}
