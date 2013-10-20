package fruit.sim;

// general utilities
import java.io.*;
import java.util.List;
import java.util.*;
import java.net.*;
import javax.tools.*;


public class Fruit
{
    private static String ROOT_DIR = "fruit";

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
    private static int DEFAULT_BOWL_SIZE = 10;

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
    static Player[] loadPlayers(String txtPath) {
		// list of players
        List <Player> playersList = new LinkedList <Player> ();

        try {
            // get file of players
            BufferedReader in = new BufferedReader(new FileReader(new File(txtPath)));
            // get tools
            URL url = Fruit.class.getProtectionDomain().getCodeSource().getLocation();
            // Create a new class loader to load the players between each game
            // so that no static fields will be carried to the next game
            ClassLoader loader = new ClassReloader(url, Fruit.class.getClassLoader());

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
                File classFile = new File(ROOT_DIR + sep + group + sep + "Player.class");
                System.err.println(classFile.getAbsolutePath());
                if (!classFile.exists() || recompile) {
                    // delete all class files
                    List <File> classFiles = directoryFiles(ROOT_DIR + sep + group, ".class");
                    System.err.print("Deleting " + classFiles.size() + " class files...   ");
                    for (File file : classFiles)
                        file.delete();
                    System.err.println("OK");
                    if (compiler == null) compiler = ToolProvider.getSystemJavaCompiler();
                    if (compiler == null) throw new Exception("Cannot load compiler");
                    if (fileManager == null) fileManager = compiler.getStandardFileManager(null, null, null);
                    if (fileManager == null) throw new Exception("Cannot load file manager");
                    // compile all files
                    List <File> javaFiles = directoryFiles(ROOT_DIR + sep + group, ".java");
                    System.err.print("Compiling " + javaFiles.size() + " source files...   ");
                    Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(javaFiles);
                    boolean ok = compiler.getTask(null, fileManager, null, null, null, units).call();
                    if (!ok) throw new Exception("Compile error");
                    System.err.println("OK");
                }
                // load class
                System.err.print("Loading player class...   ");
                String className = ROOT_DIR + "." + group + ".Player";
                Class playerClass = loader.loadClass(className);
                System.err.println("OK");
                // set name of player and append on list

                Player player = (Player) playerClass.newInstance();
                // set player id
                player.id = group.charAt(1) - '0';

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
	private static <T> void shuffle(T[] arr)
	{
		for (int i = 0 ; i != arr.length ; ++i) {
			int j = random.nextInt(arr.length - i) + i;
			T t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
		}
	}


	private static void shuffle(int[] arr)
	{
		for (int i = 0 ; i != arr.length ; ++i) {
			int j = random.nextInt(arr.length - i) + i;
			int t = arr[i];
			arr[i] = arr[j];
			arr[j] = t;
		}
	}


    // generate a random perm for a player
    private static int[] genPref()
    {
        int[] pref = new int[FRUIT_NAMES.length];
        for (int i = 0; i != pref.length; ++i)
            pref[i] = i + 1;

        shuffle(pref);
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
        int sz = 0;
        while (sz < bowlsize) {
            // pick a fruit according to current fruit distribution
            int fruit = pickFruit(); 
            int c = 1 + random.nextInt(3);
            c = Math.min(c, bowlsize - sz);
            c = Math.min(c, currentFruits[fruit]);

            bowl[fruit] += c;
            sz += c;
            currentFruits[fruit] -= c;
        }
        return bowl;
    }


    private void play() {
        for (int r = 0; r <= 1; r++) {
            System.err.println("###### ROUND " + r + " ######");
            resetRound(r);

            for (int i = 0; i < players.length; i++) {
                System.err.println("====== BOWL " + r + "." + i + " ======");

                int[] bowl = createBowl();
                System.err.println(Arrays.toString(bowl));

                int [] range = new int[players.length];
                if (r == 0) {
                    for (int k = 0; k < range.length; k++)
                        range[k] = k;
                }
                else {
                    for (int k = 0; k < range.length; k++)
                        range[k] = players.length - k - 1;
                }
                

                for (int k = 0; k < range.length; k++) {
                    int j = range[k];

                    boolean canPick = !hasBowl[j];
                    boolean mustTake = canPick && (choices[j] == 0); 
                    boolean take = players[j].pass(bowl, i, round,
                                                   canPick,
                                                   mustTake);
                    System.err.println("Bowl " + i + " is shown to player " + j);

                    // only process the return value from qualified player
                    if (canPick) {
                        if (take || mustTake) {
                            hasBowl[j] = true;
                            bowlOfPlayer[round][j] = bowl.clone();
                            System.err.println("Player " + j + " takes bowl " + i);
                            break; // break the loop
                        }
                        else {
                            System.err.println("Player " + j + " passes bowl " + i);
                            choices[j]--;
                        }
                    }
                }
            }        
        }

        computeScores();
        
        System.err.println(Arrays.toString(scores));
    }

    private int[] computeScores()
    {
        scores = new int[players.length];
        
        for (int i = 0; i < players.length; ++i) {
            int score  = 0;
            int[] pref = preference[i];

            for (int r = 0; r <= 1; r++) {
                int[] bowl = bowlOfPlayer[r][i];
                for (int k = 0; k != bowl.length; ++k)
                    score += pref[k] * bowl[k];
            }
            scores[i] = score;
        }
        
        return scores;
    }

    private void printConfig() {
        System.err.println("###### Config ######");
        System.err.println("N players: " + players.length);
        System.err.println("Bowl size: " + bowlsize);
        System.err.println("Distribution:");
        System.err.println(Arrays.toString(fruitDist));
    }


    public Fruit(Player[] players, int bowlsize, int[] dist)
    {
        this.players = players;
        this.bowlsize = bowlsize;
        this.nfruits = bowlsize * players.length;
        this.fruitDist = dist;

        // generate the preference for each player
        preference = new int[players.length][];
        for (int i = 0; i != players.length; ++i) {
            preference[i] = genPref();
            players[i].init(players.length, preference[i].clone());
        }

        // bowl of player of both rounds
        bowlOfPlayer = new int[2][players.length][];

        printConfig();
    }

    // each round has a different fruit distribution and preference
    public static void main(String[] args)
    {
        Random random = new Random();
        String playerPath = DEFAULT_PLAYERLIST;
        int bowlsize = DEFAULT_BOWL_SIZE;

        // player list
        if (args.length > 0)
            playerPath = args[0];
        if (args.length > 1)
            bowlsize = Integer.parseInt(args[1]);

        Player[] players = loadPlayers(playerPath);
        shuffle(players);


        // must be a multiple of bowlsize
        int nfruits = bowlsize * players.length;

        // read a fruit distribution
        FruitGenerator fruitgen = new UniformFruitGenerator();
        int[] dist = fruitgen.generate(nfruits);

        Fruit game = new Fruit(players, bowlsize, dist);
        game.play();
    }

    
    // reset each round
    void resetRound(int rnd) {
        round = rnd;
        bowlId = 0;

        // reset choices
        choices = new int[players.length];
        hasBowl = new boolean[players.length];
        for (int i = 0; i < players.length; i++) {
            hasBowl[i] = false;
            choices[i] = rnd == 0 ? players.length - i - 1 : i;
        }

        // create a new copy of the fruit
        currentFruits = fruitDist.clone();
    }

    // names of all fruits
    private static String[] FRUIT_NAMES = {
        "Apples", "Bananas", "Cherries", "Dates",
        "Elderberries", "Figs", "Grapes", "Honeydew",
        "Ilama", "Jackfruit", "Kiwi", "Lychee"};

    
    // all players
    private Player[] players;

    // total fruits
    private int nfruits;
    // the size of a bowl
    private int bowlsize;
    // fruit repositories
    private int[] fruitDist;

    // round number 0 or 1
    private int round = 0;
    private int bowlId = 0;
    
    // current fruit
    private int[] currentFruits;

    // the preference of each player
    private int[][] preference;
    // number of choices of player
    private int[] choices;
    // whether player has taken a bowl
    private boolean[] hasBowl;
    // bowl of player
    private int[][][] bowlOfPlayer;

    private int[] scores;

    private static Random random = new Random();
}
