package ch.dritz.zhaw.ci.tsp;

/**
 * The table containing the towns and the distances between them.Operates on the
 * lower triangular matrix since the distances mirror at the main diagonal.
 * @author D. Ritz
 */
public class Table
{
	private String[] towns;
	private int[][] distances;

	/**
	 * Initializes the table based on an array of towns and the distances
	 * @param towns
	 * @param distances
	 */
	public Table(String[] towns, int[][] distances)
	{
		this.towns = towns;
		this.distances = distances;
	}

	/**
	 * returns the size of the table (it's square)
	 * @return size of the table
	 */
	public int getSize()
	{
		return towns.length;
	}

	/**
	 * returns the name of a town at the specified index
	 * @param idx
	 * @return town name
	 */
	public String getTown(int idx)
	{
		return towns[idx % towns.length];
	}

	/**
	 * returns the distance between two towns
	 * @param idx1 index of the first town
	 * @param idx2 index of the second town
	 * @return distance or -1 if idx1 = idx2
	 */
	public int getDistance(int idx1, int idx2)
	{
		idx1 = idx1 % towns.length;
		idx2 = idx2 % towns.length;
		if (idx1 < idx2)
			return distances[idx2][idx1];
		return distances[idx1][idx2];
	}
}
