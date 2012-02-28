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
		System.out.println("============================ Binary Mutation (bool: changed) ===============================");
		for (Individual ind : me.getIndividuals()) {
			int val1 = ind.val;
			System.out.print(GeneticAlgorithm.toBitString(ind));
			System.out.print(" => ");

			GeneticAlgorithm.mutate(ind, 0.1);
			int val2 = ind.val;
			boolean changed = val1 != val2;
			System.out.print(GeneticAlgorithm.toBitString(ind));
			System.out.println(" " + changed);
		}
	}
}
