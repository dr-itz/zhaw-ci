package ch.dritz.zhaw.ci.evolutionstrategy;

import java.util.Random;

/**
 * An individual for the evolution strategy
 * @author D.Ritz
 */
public class Individual
	implements Cloneable
{
	public static final double MIN_G = 300D;
	public static final double MAX_D = 30D;
	public static final double MAX_H = 30D;

	public static Random rand = new Random();

	// object parameters
	double paramD = 0D;
	double paramH = 0D;

	// fitness
	double fitness = 0D;
	double g = 0D;
	boolean fitnessOk = false;

	// strategy related params
	int age = 1;
	double sigma = 0.01D;
	double sigmaSigma = 0.01D;

	// other
	int index = 0;

	public Individual()
	{

	}

	public Individual(int index, double d, double h)
	{
		this.index = index;
		this.paramD = d;
		this.paramH = h;
	}

	/**
	 * calculates the fitness
	 * @return true if g() >= minG
	 */
	public boolean fitness()
	{
		double d = paramD;
		double h = paramH;

		fitness = Math.PI * d * d / 2 + Math.PI * d * h;

		g = Math.PI * d * d * h / 4;
		fitnessOk = (g >= MIN_G) && (d > 0D) && (d <= MAX_D) &&
			(h > 0D) && (h <= MAX_H);

		return fitnessOk;
	}

	/**
	 * Mutates the strategic param (sigma) non-isotropic.
	 * Since it's only one param, this is extremely easy:
	 *   tau_0 and tau_1 are the same since 'u' is 1
	 */
	public void mutateStrategicParam()
	{
		sigma += sigmaSigma * rand.nextGaussian();

		double tau = 1D / Math.sqrt(2D);
		sigmaSigma = Math.exp(tau * rand.nextGaussian()) *
			sigmaSigma * Math.exp(tau * rand.nextGaussian());
	}

	/**
	 * Mutates the object params using isotropic mutation
	 */
	public void mutateObjectParams()
	{
		paramD += sigma * rand.nextGaussian();
		paramH += sigma * rand.nextGaussian();

		double tau = 1D / Math.sqrt(2D);
		sigma = sigma * Math.exp(tau * rand.nextGaussian());
	}

	/**
	 * Recombines the object parameters using average recombination
	 * @param pool
	 */
	public void recombineObjectParams(Individual[] pool)
	{
		double d = 0;
		double h = 0;
		for (Individual ind : pool) {
			d += ind.paramD;
			h += ind.paramH;
		}
		d /= pool.length;
		h /= pool.length;
		paramD = d;
		paramH = h;
	}

	@Override
	protected Individual clone()
	{
		try {
			return (Individual) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Individual at ").append(String.format("%02d", index));
		sb.append(", d: ").append(String.format("%02.3f", paramD));
		sb.append(", h: ").append(String.format("%02.3f", paramH));
		sb.append(", fit: ").append(String.format("%04.3f", fitness));
		sb.append(", g: ").append(String.format("%04.3f", g));
		sb.append(", ok: ").append(fitnessOk);
		return sb.toString();
	}

	//--------------------------------------------------------------------------

	public static Individual randomIndividual(int index)
	{
		return new Individual(index,
			rand.nextDouble() * MAX_D,
			rand.nextDouble() * MAX_H);
	}
}
