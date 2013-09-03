package fruit.sim;

// general utilities
import java.io.*;
import java.util.List;
import java.util.*;
import javax.tools.*;


public class Fruit
{
    private static String Root = "fruit";

    private static int MANY_PLAYERS = 7;

    // recompile .class file?
    private static boolean recompile = true;
    
    // print more details?
    private static boolean verbose = true;

    // Step by step trace
    private static boolean trace = true;

    // enable gui
    private static boolean gui = false;

    // default parameters
    private static final String DEFAULT_PLAYERLIST = "players.list";
    private static final String DEFAULT_DIST_PATH = "distribution.txt";
    private static int DEFAULT_CAPACITY = 10;

	// list files below a certain directory
	// can filter those having a specific extension constraint
    //
	private static List <File> directoryFiles(String path, String extension) {
		List <File> allFiles = new ArrayList <File> ();
		allFiles.add(new File(path));
		int index = 0;
		while (index != allFiles.size()) {
			File currentFile = allFiles.get(index);
			if (currentFile.isDirectory()) {
				allFiles.remove(index);
				for (File newFile : currentFile.listFiles())
					allFiles.add(newFile);
			} else if (!currentFile.getPath().endsWith(extension))
				allFiles.remove(index);
			else index++;
		}
		return allFiles;
	}

  	// compile and load players dynamically
    //
	private static Player[] loadPlayers(String txtPath) {
		// list of players
		List <Player> playersList = new LinkedList <Player> ();

        try {
            // get file of players
            BufferedReader in = new BufferedReader(new FileReader(new File(txtPath)));
            // get tools
            ClassLoader loader = Fruit.class.getClassLoader();
            if (loader == null) throw new Exception("Cannot load class loader");
            JavaCompiler compiler = null;
            StandardJavaFileManager fileManager = null;
            // get separator
            String sep = File.separator;
            // load players
            String group;
            while ((group = in.readLine()) != null) {
                System.err.println("Group: " + group);
                // search for compiled files
                File classFile = new File(Root + sep + group + sep + "Player.class");
                System.err.println(classFile.getAbsolutePath());
                if (!classFile.exists() || recompile) {
                    // delete all class files
                    List <File> classFiles = directoryFiles(Root + sep + group, ".class");
                    System.err.print("Deleting " + classFiles.size() + " class files...   ");
                    for (File file : classFiles)
                        file.delete();
                    System.err.println("OK");
                    if (compiler == null) compiler = ToolProvider.getSystemJavaCompiler();
                    if (compiler == null) throw new Exception("Cannot load compiler");
                    if (fileManager == null) fileManager = compiler.getStandardFileManager(null, null, null);
                    if (fileManager == null) throw new Exception("Cannot load file manager");
                    // compile all files
                    List <File> javaFiles = directoryFiles(Root + sep + group, ".java");
                    System.err.print("Compiling " + javaFiles.size() + " source files...   ");
                    Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(javaFiles);
                    boolean ok = compiler.getTask(null, fileManager, null, null, null, units).call();
                    if (!ok) throw new Exception("Compile error");
                    System.err.println("OK");
                }
                // load class
                System.err.print("Loading player class...   ");
                Class playerClass = loader.loadClass(Root + "." + group + ".Player");
                System.err.println("OK");
                // set name of player and append on list
                Player player = (Player) playerClass.newInstance();
                if (player == null)
                    throw new Exception("Load error");
                playersList.add(player);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

		return playersList.toArray(new Player[0]);
	}

	// shuffle array
	private static void shuffle(int[] arr, Random gen)
	{
		for (int i = 0 ; i != arr.length ; ++i) {
			int j = gen.nextInt(arr.length - i) + i;
			int t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
		}
	}


    // generate a random perm for a player
    private static int[] genPref(Random random)
    {
        int[] pref = new int[FRUIT_NAMES.length];
        for (int i = 0; i != pref.length; ++i)
            pref[i] = i;

        shuffle(pref, random);
        return pref;
    }


    private int pickFruit()
    {
        // generate a prefix sum
        int[] prefixsum = new int[FRUIT_NAMES.length];
        prefixsum[0] = currentFruits[0];
        for (int i = 1; i != FRUIT_NAMES.length; ++i)
            prefixsum[i] = prefixsum[i-1] + currentFruits[i];

        int currentFruitCount = prefixsum[FRUIT_NAMES.length-1];
        // roll a dice [0, currentFruitCount)
        int rnd = random.nextInt(currentFruitCount);
        
        for (int i = 0; i != FRUIT_NAMES.length; ++i)
            if (rnd < prefixsum[i])
                return i;

        assert false;

        return -1;
    }

    private int[] createBowl()
    {
        int[] bowl = new int[FRUIT_NAMES.length];
        int nfruits = 0;
        while (nfruits < capacity) {
            // pick a fruit according to current fruit distribution
            int fruit = pickFruit(); 
            int c = 1 + random.nextInt(3);
            c = Math.min(c, capacity - nfruits);
            c = Math.min(c, currentFruits[fruit]);

            bowl[fruit] += c;
            nfruits += c;
            currentFruits[fruit] -= c;
        }
        return bowl;
    }

    // check if player at position pos still has a chance
    private boolean hasMoreChance(int used, int pos)
    {
        return used < (players.length - pos - 1);
    }

    private int[] play() {
        int turn = 0;

        boolean[] taken = new boolean[players.length];


        // how many chances has player i used
        int[] used = new int[players.length];
        
        // until every one has a bowl
        while (turn != players.length) {
            int[] bowl = createBowl();
            
            for (int pos = 0; pos != players.length; ++pos)
                // tell each player what the bowl looks like
                players[index[pos]].tell(bowl);

            // update the 
            for (int pos = 0; pos != players.length; ++pos) {
                if (!taken[pos] &&
                    hasMoreChance(used[pos], pos)) {
                    // ask players in turn whether they want the bowl
                    boolean take = players[index[pos]].ask(bowl);
                    if (take) {
                        taken[pos] = true;
                        bowlOfPlayer[index[pos]] = bowl;
                        break;
                    }
                    else
                        used[pos]++; // use one more chance
                }
            }
        }
        return computeScores();
    }

    private int[] computeScores()
    {
        int[] scores = new int[players.length];
        
        for (int i = 0; i < players.length; ++i) {
            int score  = 0;
            int[] pref = preference[i];
            int[] bowl = bowlOfPlayer[i];
            
            for (int k = 0; k != bowl.length; ++k)
                score += pref[k] * bowl[k];

            scores[i] = score;
        }
        
        return scores;
    }


    public Fruit(Player[] players, int capacity,
                 int[] index, int[] initialFruits,
                 int[][] preference)
    {
        this.players = players;
        this.capacity = capacity;
        this.index = index;
        this.initialFruits = initialFruits;
        this.preference = preference;

        currentFruits = initialFruits.clone();
        bowlOfPlayer = new int[players.length][];

        random = new Random();
    }
                 
    private static int[] readFruitDistribution(String fruitDistPath)
    {
        return null;
    }

    // each round has a different fruit distribution and preference
    public static void main(String[] args)
    {
        Random random = new Random();
        String playerPath = DEFAULT_PLAYERLIST;
        String fruitDistPath = DEFAULT_DIST_PATH;
        int capacity = DEFAULT_CAPACITY;

        // read parameters


        // permutation generator
        Permutation permutation;

        Player[] players = loadPlayers(playerPath);

        // read fruit distribution
        int[] initialFruits = readFruitDistribution(fruitDistPath);
        
        // generate the preference for each player
        int[][] preference = new int[players.length][FRUIT_NAMES.length];
        for (int i = 0; i != players.length; ++i)
            preference[i] = genPref(random);

        int[] totalScores = new int[players.length];

        if (players.length > MANY_PLAYERS)
            permutation = new RandomPermutation(players.length);
        else
            permutation = new FullPermutation(players.length);

        // the position index of each player
        int[] index = null;
        // generate the ordering map
        // make sure every player has an equal number of turns in each position
        // A(n,n), n! permulations in total
        // for extremely large player num
        // we have to use some random shuffle generator 
        while ((index = permutation.next()) != null) {
            Fruit game = new Fruit(players, capacity, index, initialFruits, preference);
            int[] scores = game.play();
            
            aggregateScores(totalScores, scores);
        }

        printScores(totalScores);
    }

    private static void printScores(int[] scores)
    {
        
    }

    private static void aggregateScores(int[] total, int[] scores)
    {
        for (int i = 0; i != total.length; ++i)
            total[i] += scores[i];
    }

    // names of all fruits
    private static String[] FRUIT_NAMES = {
        "Apples", "Bananas", "Cherries", "Dates",
        "Elderberries", "Figs", "Grapes", "Honeydew",
        "Ilama", "Jackfruit", "Kiwi", "Lychee"};

    
    // all players
    private Player[] players;
    private int[] index;
    // the capacity of a bowl
    private int capacity;
    // the preference of each play
    private int[][] preference;
    // the initial distribution of each fruit
    private int[] initialFruits;

    // current distribution of each fruit
    private int[] currentFruits;
    private int[][] bowlOfPlayer;

    private Random random;
}
