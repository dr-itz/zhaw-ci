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
		for (int i = 0; i < num; i++)
			individuals.add(encode(i, rand.nextInt(MAX), rand.nextInt(MAX)));
	}

	private static Individual encode(int index, int d, int h)
	{
		Individual ret = new Individual();
		ret.index = index;
		ret.val = (d & MASK) << BITS | (h & MASK);
		return ret;
	}

	private static int decodeD(Individual ind)
	{
		return ind.val >> BITS;
	}

	private static int decodeH(Individual ind)
	{
		return ind.val & MASK;
	}

	public Fitness fitness(Individual ind, double max)
	{
		Fitness ret = new Fitness();
		ret.ind = ind;
		ret.start = 0;

		double d = decodeD(ind);
		double h = decodeH(ind);

		ret.fitness = Math.PI * d * d / 2 + Math.PI * d * h;

		double g = Math.PI * d * d * h / 4;
		ret.ok = g >= max;

		return ret;
	}

	public void rankSelection()
	{
		List<Fitness> fitnesses = new ArrayList<Fitness>();
		for (Individual ind : individuals) {
			Fitness fit = fitness(ind, MIN_G);
			if (fit.ok)
				fitnesses.add(fit);
		}
		Collections.sort(fitnesses, new Comparator<Fitness>() {
			@Override
			public int compare(Fitness o1, Fitness o2)
			{
				return (int) (o2.fitness - o1.fitness);
			}
		});

		int ranks = 0;
		for (int i = 0; i < fitnesses.size(); i++) {
			fitnesses.get(i).rank = i;
			ranks += i;
		}

		double start = 0;
		for (Fitness f : fitnesses) {
			start = start + 1D / (double) ranks * f.rank;
			f.start = start;
		}

		List<Individual> selectedInd = new ArrayList<GeneticAlgorithm.Individual>();
		Random rand = new Random();
		for (int i = 0; i < individuals.size(); i++) {
			double r = rand.nextDouble();
			for (Fitness f : fitnesses) {
				if (f.start >= r) {
					selectedInd.add(f.ind);
					break;
				}
			}
		}

		individuals = selectedInd;
	}

	public void show()
	{
		for (Individual ind : individuals) {
			int d = decodeD(ind);
			int h = decodeH(ind);

			Fitness fit = fitness(ind, MIN_G);
			StringBuilder sb = new StringBuilder();
			sb.append("i: ").append(ind.index);
			sb.append(", d: ").append(d);
			sb.append(", h: ").append(h);
			sb.append(", f: ").append(String.format("%1.3f", fit.fitness));
			sb.append(", ").append(fit.ok);

			System.out.println(sb);
		}
	}


	private static class Individual
	{
		int index;
		Integer val;
	}

	private static class Fitness
	{
		Individual ind;
		int rank = 0;
		double start = 0D;
		double fitness = 0D;
		boolean ok = false;
	}


	////////////////////////////////////////////////////////////////////////////
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(NUM);
		me.show();
		System.out.println("===== Rank selection =====");
		me.rankSelection();
		me.show();
	}
}
