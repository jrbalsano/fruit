package fruit.g8;

import java.util.Arrays;
import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import fruit.sim.Fruit;
import fruit.sim.FruitGenerator;
import fruit.sim.Player;

public class Test extends Fruit{


	public Test(Player[] players, int bowlsize, int[] dist) {
		super(players, bowlsize, dist);
	}
	
	public static void main(String[] args) throws Exception
    {
        Random random = new Random();
        String playerPath = "players.list";
        int bowlsize = 10;
        String distgen = "fruit.sim.UniformFruitGenerator";

        // player list
        if (args.length > 0)
            playerPath = args[0];
        // bowl size
        if (args.length > 1)
            bowlsize = Integer.parseInt(args[1]);
        // distribution
        if (args.length > 2)
            distgen = args[2];
        // enable gui?
        if (args.length > 3)
            gui = Boolean.parseBoolean(args[3]);
        if (args.length > 4)
            trace = Boolean.parseBoolean(args[4]);

        Player[] players = loadPlayers(playerPath);
        //shufflePlayer(players);

        // read a fruit distribution
        FruitGenerator fruitgen = (FruitGenerator)Class.forName(distgen).newInstance();
        //        FruitGenerator fruitgen = new fruit.dumb.FruitGenerator();

        int[] dist = fruitgen.generate(players.length, bowlsize);

        int round=100;
        int tot_score[]=new int[players.length];
        for (int i = 0; i < round; i++) {
        	Fruit game = new Fruit(players, bowlsize, dist);
            game.play(gui);
            for (int j = 0; j < tot_score.length; j++) {
				tot_score[j]+=game.scores[j];
			}
		}
        System.out.println(Arrays.toString(tot_score));
        
    }

}
