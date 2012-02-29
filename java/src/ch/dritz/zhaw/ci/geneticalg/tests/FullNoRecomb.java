package ch.dritz.zhaw.ci.geneticalg.tests;

import ch.dritz.zhaw.ci.geneticalg.GeneticAlgorithm;

/**
 * The full algorithm with
 * - 10% mutation probability
 * - NO recombination
 * @author D. Ritz
 */
public class FullNoRecomb
{
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(GeneticAlgorithm.NUM);
		me.show();

		for (int i = 0; i < 100; i++) {
			System.out.print("============= Round ");
			System.out.print(String.format("%03d", i));
			System.out.println(" ===========================================================");
			me.round(0.1D, 0);
			me.show();
		}
		me.showBest();
	}
}
