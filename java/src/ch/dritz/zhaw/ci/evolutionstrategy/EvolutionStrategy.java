package ch.dritz.zhaw.ci.evolutionstrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Implementation of a simple evolution strategy for the dimensions of cylinder
 * with minimal surface and a volume of at least 300.
 * @author D.Ritz
 */
public class EvolutionStrategy
{
	public static Random rand = new Random();

	private List<Individual> individuals;
	private List<Individual> bestList;

	private int populationSize;
	private int numOffspring;
	private int maxAge;
	private int numParents;

	private int generation;

	/**
	 * Creates the evolution strategy
	 */
	public EvolutionStrategy(int populationSize, int numOffspring, int maxAge,
			int numParents)
	{
		this.populationSize = populationSize;
		this.numOffspring = numOffspring;
		this.maxAge = maxAge;
		this.numParents = numParents;

		individuals = new ArrayList<Individual>(populationSize);
		bestList = new ArrayList<Individual>();

		for (int i = 0; i < populationSize; i++) {
			Individual ind = null;

			// ensure the initial population has only valid individuals
			do {
				ind = Individual.randomIndividual(i);
				ind.fitness();
			} while (!ind.fitnessOk);

			individuals.add(ind);
		}

		generation = 1;
	}

	/**
	 * Creates a list of indices that can be used to randomly select from another
	 * list. Implemented using LinkedList to enable O(1) removal of entries
	 * @param num number of indices in the list (from 0 to num-1)
	 * @return List
	 */
	private static List<Integer> createIndexList(int num)
	{
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < num; i++)
			list.add(i);
		return list;
	}

	/**
	 * create the pool by marriage of numParents individuals
	 * @return array with the selected individuals
	 */
	private Individual[] marriage()
	{
		Individual[] pool = new Individual[numParents];
		List<Integer> indices = createIndexList(individuals.size());
		for (int i = 0; i < numParents; i++) {
			int idx = indices.remove(rand.nextInt(indices.size()));
			pool[i] = individuals.get(idx);
		}
		return pool;
	}

	/**
	 * creates a single new Individual:
	 * - select numParents into a pool
	 * - recombine strategic params using discrete recombination
	 * - mutate strategic params with non-isotropic mutation
	 * - recombine object params using average recombination
	 * - mutate object params using isotroptic mutation (=> new Individual)
	 * - calcualte fitness of new Individual
	 * @return the new offspring Individual
	 */
	private Individual createOneOffspring()
	{
		// create the pool
		Individual[] pool = marriage();

		// recombine object params using average recombination
		Individual ind = new Individual();
		ind.recombineObjectParams(pool);

		/*
		 * Recombine strategic params using discrete recombination.
		 * ie. just choose from the pool.
		 */
		int idx = rand.nextInt(pool.length);
		ind.sigmaSigma = pool[idx].sigmaSigma;
		ind.sigma = pool[idx].sigma;

		// mutate strategic params with non-isotropic mutation
		ind.mutateStrategicParam();

		// mutate object params using isotroptic mutation
		ind.mutateObjectParams();

		// re-calculate the fitness
		ind.fitness();

		return ind;
	}

	/**
	 * Performs a rank based selection:
	 * - age current population, drop those too old
	 * - mix with the valid offspring
	 * - rank based selection
	 * - prints the best individual
	 * @param offsprings
	 */
	private void selection(List<Individual> offsprings)
	{
		// first age current population, mix them with fit offspring
		List<Individual> mixedGen = new ArrayList<Individual>();
		for (Individual ind : individuals) {
			ind.age++;
			if (ind.age <= maxAge)
				mixedGen.add(ind);
		}
		for (Individual ind : offsprings) {
			if (ind.fitnessOk)
				mixedGen.add(ind);
		}

		// perform a tournament selection, saving the best to bestList
		List<Integer> indices1 = createIndexList(mixedGen.size());
		List<Integer> indices2 = createIndexList(mixedGen.size());

		Individual best = null;
		List<Individual> selectedInd = new ArrayList<Individual>();
		for (int i = 0; i < populationSize; i++) {
			int r = rand.nextInt(indices1.size());
			int idx1 = indices1.remove(r);

			r = rand.nextInt(indices2.size());
			int idx2 = indices2.remove(r);

			Individual ind1 = mixedGen.get(idx1);
			Individual ind2 = mixedGen.get(idx2);

			Individual sel = ind1.fitness < ind2.fitness ? ind1 : ind2;
			sel = sel.clone();
			sel.index = i;
			selectedInd.add(sel);

			if (best == null || sel.fitness < best.fitness)
				best = sel;
		}
		bestList.add(best.clone());

		// replace current with new generation
		individuals = selectedInd;

		printBestIndividual(generation, best);
	}

	/**
	 * one round creating a new generation
	 * - create numOffsprings new individuals
	 * - select from current generation and offsprings to create new generation
	 */
	public void newGeneration()
	{
		// create offsprings
		List<Individual> offsprings = new ArrayList<Individual>();
		for (int i = 0; i < numOffspring; i++)
			offsprings.add(createOneOffspring());

		// select
		selection(offsprings);

		generation++;
	}

	/**
	 * @return the generation
	 */
	public int getGeneration()
	{
		return generation;
	}

	private void printBestIndividual(int gen, Individual ind)
	{
		System.out.print("generation: ");
		System.out.print(String.format("%03d", gen));
		System.out.print(" BEST: ");
		System.out.println(ind);
	}

	/**
	 * Shows the best individuals from each round
	 */
	public void showBest()
	{
		for (int i = 0; i < bestList.size(); i++)
			printBestIndividual(i, bestList.get(i));
	}
}
