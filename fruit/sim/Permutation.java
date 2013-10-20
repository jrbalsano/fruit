package fruit.sim;

import java.util.*;

// generate the ordering map
// make sure every player has an equal number of turns in each position
// A(n,n), n! permulations in total
// for extremely large player num
// we have to use some random shuffle generator 
interface Permutation
{

    public int[] next();
}

class FullPermutation implements Permutation
{
    private int p[];

    public FullPermutation(int nums)
    {
        for (int i = 0; i != nums; ++i)
            p[i] = i;
    }

    public int[] next()
    {
        int a = p.length - 2;
        while (a >= 0 && p[a] >= p[a + 1]) {
            a--;
        }
        if (a == -1) {
            return null;
        }
        int b = p.length - 1;
        while (p[b] <= p[a]) {
            b--;
        }
        int t = p[a];
        p[a] = p[b];
        p[b] = t;
        for (int i = a + 1, j = p.length - 1; i < j; i++, j--) {
            t = p[i];
            p[i] = p[j];
            p[j] = t;
        }
        return p;
    }
}

class RandomPermutation implements Permutation
{
    private static final int MAX_REPEATS = 10000;
    private Random random;
    private int repeat;
    private int p[];

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

    public RandomPermutation(int nums)
    {
        repeat = MAX_REPEATS;
        random = new Random();
        for (int i = 0; i != nums; ++i)
            p[i] = i;
    }

    public int[] next()
    {
        if (repeat-- > 0) {
            shuffle(p, random);
            return p;
        }
        else
            return null;
    }
}
