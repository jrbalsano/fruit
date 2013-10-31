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
        	magic=magic_table[nplayers-position];
        else
        	magic=(int) Math.round(0.369*(nplayers-position) );
    }
    int max=0;
    int counter=0;
    int bowSize=0;
    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
    	counter++;
    	if(counter==1){
    		for(int i=0;i<12;i++){
        		bowSize+=bowl[i];
        	}
    		//update(bowSize*6);
    	}
    	//System.out.printf("\n counter is %d\n", counter);
    	//record[counter-1]=score(bowl);
    	update(score(bowl));
    	
    	if (musTake){
			return true;
		}
    	
    	//no enough information
    	if (info.size()<=1) {
			return false;
		}
    	
    	int futureBowls=0;
    	if (round==0) {
    		futureBowls=nplayer-position-counter;
            //return round0(bowl,bowlId,round,canPick,musTake);
        } else {
        	futureBowls=position+1-counter;
            //return round1(bowl,bowlId,round,canPick,musTake);
        }
    	double b=score(bowl);
    	double PrB=1, p=probLessThan(b);
    	for (int i = 0; i < futureBowls; i++) {
			PrB*=p;
		}
    	double PrA=1-PrB;
    	double ExA=exptGreaterThan(b);
    	double ExB=exptLessThan(b);
    	double Ex2=PrA*ExA+PrB*ExB;
    	if(Ex2>b) { //
    		return false;
    	}
    	else {
			return true;
		}
    }
    private double exptLessThan(double b) {
    	double sum=0,interval=(b-12)/10000,val=12;
		for (int i = 0; i <10000; i++) {
			sum+=phi(val, mu, sigma)*val*interval;
			val+=interval;
		}
		return sum/probLessThan(b);
	}
	private double exptGreaterThan(double b) {
		double sum=0,interval=(bowSize*12-b)/10000,val=b;
		for (int i = 0; i <10000; i++) {
			sum+=phi(val, mu, sigma)*val*interval;
			val+=interval;
		}
		return sum/(1-probLessThan(b));
	}
	private double probLessThan(double b) {
		if (sigma<1e-8) {
			if (b>mu) {
				return 1;
			}else {
				return 0;
			}
		}
		return Phi((b - mu) / sigma);
	}
    
    public static double Phi(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * phi(z);
    }
    
    // return phi(x) = standard Gaussian pdf
    public static double phi(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }
    public static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }
    
	ArrayList<Integer> info=new ArrayList<Integer>();
    double mu,sigma;
	private void update(int x) {
		info.add(x);
		mu=0;
		for(Integer y:info){
			mu+=y;
		}
		mu=mu/info.size();
		sigma=0;
		for(Integer y:info){
			sigma+=(y-mu)*(y-mu);
		}
		sigma/=info.size();
		sigma=Math.sqrt(sigma);
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
