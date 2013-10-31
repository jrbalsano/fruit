package fruit.sim;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

public class StatisticsCollector {
  static final String playerPath = "players";
  static final String distributionPath = "dist";
  static int[] bowlsizes = {5, 10, 25, 50, 100 };
  static final int REPEATS = 500;
  static int bowlSize;
  String distPath;

  public static void main(String[] args) throws Exception {
    String resultsFile = "results.txt";
    if (args.length > 0) {
      resultsFile = args[0];
    }
    File[] playerFiles = getFiles(playerPath);
    File[] distributionFiles = getFiles(distributionPath);
    PrintWriter writer = new PrintWriter(resultsFile, "UTF-8");
    Fruit.trace = false;
    for (int bowlsize : bowlsizes) {
      for (File playerList : playerFiles) {
        for (File fruitDistributionFile : distributionFiles) {
          try {
            String[] playerNames = Fruit.loadPlayerNames(playerList.getPath());
            
            // Set up result object
            Result configResult = new Result();
            configResult.playernames = playerNames;
            configResult.playerlist = playerList.getPath();
            configResult.distribfile = fruitDistributionFile.getPath();
            configResult.bowlsize = bowlsize;
            configResult.playerAverageScores = new double[playerNames.length];
            
            // Repeat run the configuration
            for (int i = 0; i < REPEATS; i++) {
              Player[] players = Fruit.loadPlayers(playerList.getPath());
              Fruit.shufflePlayer(players);
              int[] distribution = Fruit.createServingBowl(fruitDistributionFile.getPath(), bowlsize * players.length);

              Fruit game = new Fruit(players, bowlsize, distribution);
              game.play(false);

              for (int p = 0; p < players.length; p++) {
                configResult.playerAverageScores[players[p].id] += game.scores[p];
              }

            }
            
            // Calculate average scores
            for (int p = 0; p < playerNames.length; p++) {
              configResult.playerAverageScores[p] /= REPEATS;
            }
            
            // Write the results
            writer.write(configResult.toString());
            writer.flush();
          }
          catch (Exception e) {
            writer.write(e.getMessage() + "\n");
            writer.flush();
          }
        }
      }
    }
    writer.close();
  }

  public static File[] getFiles(String path) {
    File folder = new File(path);
    System.out.println(folder.getAbsolutePath());
    return folder.listFiles();
  }

  private static class Result {
    public String playerlist;
    public String distribfile;
    public int bowlsize;
    public String[] playernames;
    public double[] playerAverageScores;

    public String toString() {
      String str = "";
      str += "Players: " + playerlist + "\nDistribution: " + distribfile
          + "\nBowl Size: " + bowlsize;
      for (int i = 0; i < playernames.length; i++) {
        str += "\n\t" + playernames[i] + ": " + playerAverageScores[i];
      }
      str += "\n";
      return str;
    }
  }
}
