package ch.dritz.zhaw.ci.geneticalg.tests;

import ch.dritz.zhaw.ci.geneticalg.GeneticAlgorithm;

/**
 * The full algorithm with
 * - 1% mutation probability
 * - NO recombination
 * @author D. Ritz
 */
public class FullP1NoRecomb
{
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(GeneticAlgorithm.NUM);
		me.show();

		for (int i = 0; i < 100; i++) {
			System.out.print("============= Round ");
			System.out.print(String.format("%03d", i));
			System.out.println(" ===========================================================");
			me.round(0.01D, 0);
			me.show();
		}
		me.showBest();
	}
}
