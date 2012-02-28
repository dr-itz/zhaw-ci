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

	List<Individual> individuals;

	public GeneticAlgorithm(int num)
	{
		individuals = new ArrayList<Individual>(num);

		Random rand = new Random();
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
				return (int) (o2.fitness - o1.fitness);
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
		Random rand = new Random();
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

	public void show()
	{
		for (Individual ind : individuals) {
			ind.fitness(MIN_G);
			System.out.println(ind);
		}
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
			return val >> BITS;
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
