package ch.dritz.zhaw.ci.geneticalg.tests;

import ch.dritz.zhaw.ci.geneticalg.GeneticAlgorithm;
import ch.dritz.zhaw.ci.geneticalg.GeneticAlgorithm.Individual;

/**
 * @author D. Ritz
 */
public class BinaryMutation
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(GeneticAlgorithm.NUM);
		System.out.println("============================ Binary Mutation ===============================");
		for (Individual ind : me.getIndividuals()) {
			System.out.print(GeneticAlgorithm.toBitString(ind));
			System.out.print(" => ");

			GeneticAlgorithm.mutate(ind, 0.1);
			System.out.println(GeneticAlgorithm.toBitString(ind));
		}
	}
}
