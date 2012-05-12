package ch.dritz.zhaw.ci.tsp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Solves the traveling salesman problem using simulated annealing
 * @author D. Ritz
 */
public class TravelingSalesman
{
	private static final int START_TOWN = 0; // where the tour starts

	private Table table;
	private int size;

	public TravelingSalesman(File file)
		throws IOException
	{
		table = Parser.parse(file);
		size = table.getSize();
	}

	/**
	 * calculates the initial path using a nearest-neighbor heuristic, starting
	 * with the first town
	 */
	private Path initialize()
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
		return ret;
	}

	/**
	 * finds a solution to the TSP using simulated annealing
	 */
	public Path findSolution()
	{
		Path s = initialize();
		Path sBest = s;
		/*
		 * FIXME: simulated annealing using 2-opt
		int n = 0;
		int t = 300;
		while (t > 20) {
			while (inner condition) {
				sNew = generatePath(s);
				s = selectMetropolis(s, sNew, T);
				if (s.measure() < sBest.measure() {
					sBest = s;
				}
			}
			t = newTemp(t);
			n++;
		}
		*/
		return sBest;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws IOException
	{
		if (args.length != 1) {
			System.err.println("Usage: TravelingSalesman <input-file>");
			System.exit(1);
		}

		TravelingSalesman tsp = new TravelingSalesman(new File(args[0]));
		Path s = tsp.findSolution();
		System.out.println(s);

		// FIXME: remove, testing only
		Path s2 = s.apply2opt(10, 15);
		System.out.println(s2);
	}
}
