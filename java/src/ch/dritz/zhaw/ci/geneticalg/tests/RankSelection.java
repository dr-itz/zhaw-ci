package ch.dritz.zhaw.ci.geneticalg.tests;

import ch.dritz.zhaw.ci.geneticalg.GeneticAlgorithm;

/**
 * @author D. Ritz
 */
public class RankSelection
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GeneticAlgorithm me = new GeneticAlgorithm(GeneticAlgorithm.NUM);
		me.show();
		System.out.println(" ============================ Rank Selection ===============================");
		me.rankSelection();
		me.show();
	}
}
