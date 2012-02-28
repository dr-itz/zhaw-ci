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
	private static final int BITS = 5;
	private static final int NUM = 30;
	private static final double MIN_G = 300D;

	private static final int MAX = 1 << BITS;
	private static final int MASK = MAX - 1;

	private static Random rand = new Random();

	private List<Individual> individuals;
	private List<Individual> bestList;


	public GeneticAlgorithm(int num)
	{
		individuals = new ArrayList<Individual>(num);
		bestList = new ArrayList<Individual>();

		for (int i = 0; i < num; i++) {
			individuals.add(
				Individual.encode(i, rand.nextInt(MAX), rand.nextInt(MAX)));
		}
	}

	public void rankSelection()
	{
		// calculate fitness, filter
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
			start += 1D / (double) ranks * ind.rank;
		}

		// randomly select individuals
		List<Individual> selectedInd = new ArrayList<Individual>();
		for (int i = 0; i < individuals.size(); i++) {
			double r = rand.nextDouble();
			for (int j = okList.size() - 1; j >= 0; j--) {
				Individual ind = okList.get(j);
				if (ind.start <= r) {
					selectedInd.add(ind);
					break;
				}
			}
		}

		individuals = selectedInd;
	}

	/**
	 * Recombines the two individuals
	 * @param ind1
	 * @param ind2
	 */
	public static void recombine(Individual ind1, Individual ind2)
	{
		int where = rand.nextInt(2 * BITS - 2) + 1;
		int mask1 = where - 1;
		int mask2 = ~mask1;

		int new1 = (ind1.val & mask1) | (ind2.val & mask2);
		int new2 = (ind1.val & mask2) | (ind2.val & mask1);

		ind1.reset();
		ind2.reset();
		ind1.val = new1;
		ind2.val = new2;
	}

	/**
	 * mutates (flips) the bit at the given position
	 * @param ind
	 * @param bit
	 */
	public static void mutate(Individual ind, int bit)
	{
		int mask = 1 << bit;
		ind.val ^= mask;
	}

	public void show()
	{
		for (Individual ind : individuals) {
			ind.fitness(MIN_G);
			System.out.println(ind);
		}
	}

	public void saveBest()
	{
		Individual best = null;
		for (Individual ind : individuals) {
			if (!ind.fitness(MIN_G))
				continue;
			if (best == null || ind.fitness < best.fitness)
				best = ind;
		}
		bestList.add(best.duplicate());
	}

	private static class Individual
	{
		int index;
		Integer val;

		int rank = 0;
		double start = 0D;
		double fitness = 0D;
		boolean ok = false;

		private static Individual encode(int index, int d, int h)
		{
			Individual ret = new Individual();
			ret.index = index;
			ret.val = (d & MASK) << BITS | (h & MASK);
			return ret;
		}

		public int decodeD()
		{
			return (val >> BITS) & MASK;
		}

		public int decodeH()
		{
			return val & MASK;
		}

		public boolean fitness(double max)
		{
			double d = decodeD();
			double h = decodeH();

			fitness = Math.PI * d * d / 2 + Math.PI * d * h;

			double g = Math.PI * d * d * h / 4;
			ok = g >= max;

			return ok;
		}

		public void reset()
		{
			start = 0D;
			rank = 0;
			fitness = 0D;
			ok = false;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Individual at ").append(String.format("%02d", index));
			sb.append(", value: ").append(String.format("%03x", val));
			sb.append(", d: ").append(String.format("%02d", decodeD()));
			sb.append(", h: ").append(String.format("%02d", decodeH()));
			sb.append(", fit: ").append(String.format("%04.3f", fitness));
			sb.append(", ok: ").append(ok);
			sb.append(", rank: ").append(rank);
			sb.append(", start: ").append(String.format("%1.3f", start));

			return sb.toString();
		}

		public Individual duplicate()
		{
			Individual ind = new Individual();
			ind.val = val;
			return ind;
		}
	}


	////////////////////////////////////////////////////////////////////////////
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(NUM);
		me.show();
		System.out.println("===================================== Rank selection =====================================");
		me.rankSelection();
		me.show();
	}
}
