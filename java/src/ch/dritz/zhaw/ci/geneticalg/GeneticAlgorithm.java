package ch.dritz.zhaw.ci.geneticalg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Genetic algorithm for dimensions of cylinder with minimal surface and a
 * volume of at least 300. Implemented with:
 * - Individuals with 10bits (5bit diameter, 5bit height)
 * - Rank based selection
 *
 * @author D. Ritz
 */
public class GeneticAlgorithm
{
	public static final int BITS = 5;
	public static final int NUM = 30;
	public static final double MIN_G = 300D;

	public static final int MAX = 1 << BITS;
	public static final int MASK = MAX - 1;

	public static Random rand = new Random();

	private List<Individual> individuals;
	private List<Individual> bestList;


	/**
	 * Creates the genetic algorithm with num random elements
	 * @param num Number of random elements
	 */
	public GeneticAlgorithm(int num)
	{
		individuals = new ArrayList<Individual>(num);
		bestList = new ArrayList<Individual>();

		for (int i = 0; i < num; i++) {
			individuals.add(
				Individual.encode(i, rand.nextInt(MAX), rand.nextInt(MAX)));
		}
	}

	/**
	 * Executes a rank based selection on all elements
	 */
	public void rankSelection()
	{
		// calculate fitness, filter invalid elements
		List<Individual> okList = new ArrayList<Individual>();
		for (Individual ind : individuals) {
			if (ind.fitness(MIN_G))
				okList.add(ind);
		}

		// sort in preparation to calculate rank
		Collections.sort(okList, new Comparator<Individual>() {
			@Override
			public int compare(Individual o1, Individual o2)
			{
				/*
				 * We want to assign the highest rank the lowest fitness value
				 * because we want to minimize f().
				 * This sorts the biggest element first.
				 */
				return (int) Double.compare(o2.fitness, o1.fitness);
			}
		});

		// set rank based on position in sorted list
		int ranks = 0;
		for (int i = 0; i < okList.size(); i++) {
			okList.get(i).rank = i + 1;
			ranks += i + 1;
		}

		// calculate a start value between 0.0 and 1.0 based on rank
		double start = 0;
		for (Individual ind : okList) {
			ind.start = start;
			start += 1D / (double) ranks * (double) ind.rank;
		}

		// randomly select individuals
		List<Individual> selectedInd = new ArrayList<Individual>();
		for (int i = 0; i < individuals.size(); i++) {
			double r = rand.nextDouble();
			for (int j = okList.size() - 1; j >= 0; j--) {
				Individual ind = okList.get(j);
				if (ind.start <= r) {
					Individual tmp = ind.duplicate();
					tmp.index = i;
					selectedInd.add(tmp);
					break;
				}
			}
		}

		individuals = selectedInd;
	}

	/**
	 * Recombines the two individuals using single point recombination at a
	 * random position
	 * @param ind1
	 * @param ind2
	 */
	public static void recombine(Individual ind1, Individual ind2)
	{
		/*
		 * random position, ensuring at least one bit on the left and the right
		 * is kept, otherwise a simple swap could happen (it still can depending
		 * on the actual bits, but with lower probability)
		 */
		int where = rand.nextInt(2 * BITS - 2) + 1;
		int mask1 = (1 << where) - 1;    // the lower bits
		int mask2 = ~mask1 & 0x7FFFFFFF; // the higher bits

		int new1 = (ind1.val & mask2) | (ind2.val & mask1);
		int new2 = (ind1.val & mask1) | (ind2.val & mask2);

		ind1.reset();
		ind2.reset();
		ind1.val = new1;
		ind2.val = new2;
	}

	/**
	 * Recombine numPairs pairs with each other
	 * @param numPairs
	 */
	public void recombine(int numPairs)
	{
		// create a list with all indices
		List<Integer> indices = new ArrayList<Integer>(individuals.size());
		for (int i = 0; i < individuals.size(); i++)
			indices.add(i);

		for (int i = 0; i < numPairs; i++) {
			/*
			 * get a random index from the list and remove to ensure each index
			 * is only used once
			 */
			int r = rand.nextInt(indices.size());
			int idx1 = indices.remove(r);

			// again..
			r = rand.nextInt(indices.size());
			int idx2 = indices.remove(r);

			recombine(individuals.get(idx1), individuals.get(idx2));
		}
	}

	/**
	 * for each bit in the individual, flip with the given probability
	 * @param ind
	 * @param prob
	 * @return true if at least one bit changed, false otherwise
	 */
	public static boolean mutate(Individual ind, double prob)
	{
		int old = ind.val;
		int mask = 1;
		for (int i = 0; i < BITS; i++) {
			double r = rand.nextDouble();
			if (r < prob) {
				ind.val ^= mask;
			}
			mask <<= 1;
		}
		ind.fitness(MIN_G);
		return old != ind.val;
	}

	/**
	 * Iterates over all individuals, mutates them
	 * @param prob
	 * @param minOne if true, ensures at least one mutated individual
	 * @return Number of mutated individuals
	 */
	public int mutate(double prob, boolean minOne)
	{
		int mutated = 0;
		do {
			for (Individual ind : individuals) {
				if (mutate(ind, prob))
					mutated++;
			}
		} while (minOne && mutated == 0);
		return mutated;
	}

	/**
	 * Shows all individuals
	 */
	public void show()
	{
		for (Individual ind : individuals) {
			ind.fitness(MIN_G);
			System.out.println(ind);
		}
	}

	/**
	 * Saves the individual with the best (lowest) fitness value
	 */
	public void saveBest()
	{
		Individual best = null;
		for (Individual ind : individuals) {
			if (!ind.ok)
				continue;
			if (best == null || ind.fitness < best.fitness)
				best = ind;
		}
		if (best != null) {
			Individual tmp = best.duplicate();
			tmp.index = best.index;
			bestList.add(tmp);
		}
	}

	/**
	 * Shows the best individuals from each round
	 */
	public void showBest()
	{
		for (int i = 0; i < bestList.size(); i++) {
			System.out.print("round: ");
			System.out.print(String.format("%03d", i));
			System.out.print(" BEST: ");
			System.out.println(bestList.get(i));
		}
	}

	/**
	 * Execute a whole round
	 * - calculate fitness (as part of rank selection)
	 * - perform rank selection
	 * - optionally recombine some individuals
	 * - mutate (and re-calculate fitness)
	 * - save best
	 * @param mutationProb
	 * @param recombinePairs
	 */
	public void round(double mutationProb, int recombinePairs)
	{
		rankSelection();
		if (recombinePairs > 0)
			recombine(recombinePairs);
		int mutated = mutate(mutationProb, recombinePairs == 0);
		System.out.println("Mutated: " + mutated);
		saveBest();
	}

	public List<Individual> getIndividuals()
	{
		return individuals;
	}

	/**
	 * A single individual. The value is binary encoded in a simple int, using
	 * bit operations to access the bits.
	 * @author D.Ritz
	 */
	public static class Individual
	{
		int index;
		Integer val;

		int rank = 0;
		double start = 0D;
		double fitness = 0D;
		double g = 0D;
		boolean ok = false;

		/**
		 * Helper to create a new individual with the given values for d, h
		 * @param index where in the list this will be
		 * @param d d
		 * @param h h
		 * @return new individual
		 */
		public static Individual encode(int index, int d, int h)
		{
			Individual ret = new Individual();
			ret.index = index;
			ret.val = (d & MASK) << BITS | (h & MASK);
			return ret;
		}

		/**
		 * Decodes 'd' from the value
		 * @return d
		 */
		public int decodeD()
		{
			return (val >> BITS) & MASK;
		}

		/**
		 * Decodes 'h' from the value
		 * @return h
		 */
		public int decodeH()
		{
			return val & MASK;
		}

		/**
		 * calculates the fitness
		 * @param minG the minimal value allowed for g()
		 * @return true if g() >= minG
		 */
		public boolean fitness(double minG)
		{
			double d = decodeD();
			double h = decodeH();

			fitness = Math.PI * d * d / 2 + Math.PI * d * h;

			g = Math.PI * d * d * h / 4;
			ok = g >= minG;

			return ok;
		}

		/**
		 * resets everything but the actual value
		 */
		public void reset()
		{
			start = 0D;
			rank = 0;
			fitness = 0D;
			g = 0D;
			ok = false;
		}

		public Integer getVal()
		{
			return val;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Individual at ").append(String.format("%02d", index));
			sb.append(", value: ").append(String.format("%03x", val));
			sb.append("(").append(toBitString(this)).append(")");
			sb.append(", d: ").append(String.format("%02d", decodeD()));
			sb.append(", h: ").append(String.format("%02d", decodeH()));
			sb.append(", fit: ").append(String.format("%04.3f", fitness));
			sb.append(", g: ").append(String.format("%04.3f", g));
			sb.append(", ok: ").append(ok);
			sb.append(", rank: ").append(rank);
			sb.append(", start: ").append(String.format("%1.3f", start));

			return sb.toString();
		}

		/**
		 * Clones the individual
		 * @return A copy of the individual with only the important values copied
		 */
		public Individual duplicate()
		{
			Individual ind = new Individual();
			ind.val = val;
			ind.fitness = fitness;
			ind.g = g;
			ind.ok = ok;
			ind.rank = rank;
			ind.start = start;
			return ind;
		}
	}

	/**
	 * Returns a String representing the bits in the individual
	 * @param ind The individual
	 * @return The bit string
	 */
	public static String toBitString(Individual ind)
	{
		StringBuilder sb = new StringBuilder();
		int mask = 1 << 2*BITS;
		for (int i = 0; i < 2*BITS; i++) {
			if ((ind.val & mask) != 0)
				sb.append("1");
			else
				sb.append("0");
			mask >>= 1;
		}
		return sb.toString();
	}
}
