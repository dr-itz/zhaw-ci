package ch.dritz.zhaw.ci.tsp;

/**
 * @author D. Ritz
 */
public class Table
{
	private String[] towns;
	private int[][] distances;

	public Table(String[] towns, int[][] distances)
	{
		this.towns = towns;
		this.distances = distances;
	}

	public int getSize()
	{
		return towns.length;
	}

	public String getTown(int idx)
	{
		return towns[idx];
	}

	public int getDistance(int idx1, int idx2)
	{
		if (idx1 < idx2)
			return distances[idx2][idx1];
		return distances[idx1][idx2];
	}
}
