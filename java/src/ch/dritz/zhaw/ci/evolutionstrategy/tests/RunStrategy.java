package ch.dritz.zhaw.ci.evolutionstrategy.tests;

import java.util.List;

import ch.dritz.zhaw.ci.evolutionstrategy.EvolutionStrategy;
import ch.dritz.zhaw.ci.evolutionstrategy.Individual;

/**
 * Runs the evolution strategy with the specified params
 * @author D.Ritz
 */
public class RunStrategy
{
	public static void main(String[] args)
	{
		EvolutionStrategy es = new EvolutionStrategy(7, 49, 15, 3);

		List<Individual> initialPopulation = es.getIndividuals();
		for (Individual ind : initialPopulation)
			System.out.println(ind);

		for (int i = 0; i < 100; i++)
			es.newGeneration();
	}
}
