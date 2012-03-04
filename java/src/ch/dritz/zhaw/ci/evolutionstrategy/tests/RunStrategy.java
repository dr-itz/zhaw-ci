package ch.dritz.zhaw.ci.evolutionstrategy.tests;

import ch.dritz.zhaw.ci.evolutionstrategy.EvolutionStrategy;

/**
 * Runs the evolution strategy with the specified params
 * @author D.Ritz
 */
public class RunStrategy
{
	public static void main(String[] args)
	{
		EvolutionStrategy es = new EvolutionStrategy(7, 49, 15, 3);
		for (int i = 0; i < 100; i++) {
			es.newGeneration();
		}
	}
}
