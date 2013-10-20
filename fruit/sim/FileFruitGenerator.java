package fruit.sim;

public class FileFruitGenerator
{
    static String fruitDistPath = "dist.txt";

    public int[] generate(int nfruits) {
        int[] dist = new int[Fruit.FRUIT_NAMES.length];
        int left = nfruits;
        try {
            Scanner in = new Scanner(new BufferedReader(new FileReader(fruitDistPath)));
            for (int i = 0; i != Fruit.FRUIT_NAMES.length - 1; ++i) {
                double ratio = in.nextDouble();
                dist[i] = nfruits * ratio;
                left -= dist[i];
            }
            assert left >= 0;

            dist[11] = left;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            in.close();
        }
        return dist;
    }
}
