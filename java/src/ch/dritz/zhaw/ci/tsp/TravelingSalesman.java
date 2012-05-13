package ch.dritz.zhaw.ci.tsp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Solves the traveling salesman problem using simulated annealing
 * @author D. Ritz
 */
public strictfp class TravelingSalesman
{
	private static final int START_TOWN = 0; // where the tour starts
	private static final double CONSTANT = 1.0;

	private Table table;
	private int size;
	private Random rand;
	private Path initialPath;
	private Path bestPath;
	private List<Thread> threads;

	public TravelingSalesman(File file)
		throws IOException
	{
		table = Parser.parse(file);
		size = table.getSize();
		rand = new Random();
		threads = new ArrayList<Thread>();

		initialize();
	}

	/**
	 * calculates the initial path using a nearest-neighbor heuristic, starting
	 * with the first town
	 */
	private void initialize()
	{
		Path ret = new Path(table);

		Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
		ret.setTownAtPosition(0, START_TOWN);
		visited.put(START_TOWN, Boolean.TRUE);

		int prevIdx = START_TOWN;
		// outer loop: elements in the path
		for (int i = 1; i < size; i++) {
			int minDist = Integer.MAX_VALUE;
			int minIdx = 0;
			// inner loop: find smallest distance
			for (int j = 0; j < size; j++) {
				if (visited.containsKey(j))
					continue;
				int distance = table.getDistance(prevIdx, j);
				if (distance < minDist) {
					minDist = distance;
					minIdx = j;
				}
			}

			ret.setTownAtPosition(i, minIdx);
			visited.put(minIdx, Boolean.TRUE);
			prevIdx = minIdx;
		}
		initialPath = ret;
		bestPath = ret;
	}

	/**
	 * @return the initialPath
	 */
	public Path getInitialPath()
	{
		return initialPath;
	}

	/**
	 * randomly applies 2-opt to generate a new path
	 * @param s current path
	 * @return new path
	 */
	private Path generatePath(Path s)
	{
		Path sNew = null;
		do  {
			int i1 = rand.nextInt(s.getSize());
			int i2 = rand.nextInt(s.getSize());
			sNew =  s.apply2opt(i1, i2);
		} while (sNew == null);
		return sNew;
	}

	/**
	 * selects between the old and the new path using the metropolis function
	 * @param s the current path
	 * @param sNew the new path
	 * @param temperature the current temperature
	 * @return the selected path
	 */
	private Path metropolisSelection(Path s, Path sNew, double temperature)
	{
		if (sNew.measure() < s.measure())
			return sNew;

		double diff = (double) s.measure() - sNew.measure();
		double exp = (diff / (temperature * CONSTANT));
		double m = Math.exp(exp);
		double r = rand.nextDouble();
		if (m >= r)
			return sNew;
		return s;
	}

	private double newTemp(double temperature)
	{
//		return temperature - 5D;
		return temperature * 0.99;
	}

	/**
	 * finds a solution to the TSP using simulated annealing
	 */
	public Path findSolution()
	{
		Path s = initialPath;
		Path sBest = s;
		double t = 300D;
		while (t > 20) {
			s = sBest;
			for (int i = 0; i < 200; i++) {
				Path sNew = generatePath(s);
				s = metropolisSelection(s, sNew, t);
				if (s.measure() < sBest.measure())
					sBest = s;
			}
			t = newTemp(t);
		}

		setBestPath(sBest);
		return sBest;
	}

	private synchronized void setBestPath(Path path)
	{
		if (path.measure() < bestPath.measure())
			bestPath = path;
	}

	public void findBestOutOf(final int number)
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run()
			{
				for (int i = 0; i < number; i++)
					findSolution();
			}
		});
		threads.add(t);
		t.start();
	}

	public void waitFinished()
	{
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// don't care
			}
		}
	}

	/**
	 * @return the bestPath
	 */
	public synchronized Path getBestPath()
	{
		return bestPath;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws IOException
	{
		if (args.length < 1) {
			System.err.println("Usage: TravelingSalesman <input-file> [<threads> [<iterations>]]");
			System.exit(1);
		}

		int numThreads = 2;
		int numIter = 10;
		if (args.length > 1)
			numThreads = Integer.parseInt(args[1]);
		if (args.length > 2)
			numIter = Integer.parseInt(args[2]);

		TravelingSalesman tsp = new TravelingSalesman(new File(args[0]));
		Path initial = tsp.getInitialPath();
		System.out.print("INITIAL : ");
		System.out.println(initial);

		for (int i = 0; i < numThreads; i++)
			tsp.findBestOutOf(numIter);

		tsp.waitFinished();
		System.out.print("BEST: ");
		System.out.println(tsp.getBestPath());
	}
}
