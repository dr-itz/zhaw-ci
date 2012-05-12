package ch.dritz.zhaw.ci.tsp;

/**
 * One possible path
 * @author D.Ritz
 */
public class Path
{
	private Table table;
	private int[] order;
	private int totalDist;

	/**
	 * constructs an empty (invalid) path for the specified table
	 * @param table
	 */
	public Path(Table table)
	{
		this.table = table;
		order = new int[table.getSize()];
		totalDist = 0;
	}

	/**
	 * returns the size of the path, i.e. number of towns
	 * @return size
	 */
	public int getSize()
	{
		return order.length;
	}

	/**
	 * sets the town at the specified position
	 * @param position the position in the path
	 * @param townIndex the index of the town
	 */
	public void setTownAtPosition(int position, int townIndex)
	{
		order[position % order.length] = townIndex;
		totalDist = 0;
	}

	/**
	 * returns the town at the specified position
	 * @param position
	 * @return town index
	 */
	public int getTownAtPosition(int position)
	{
		return order[position % order.length];
	}

	/**
	 * measures the total distance from start trough all towns back to start
	 * @return total distance
	 */
	public int measure()
	{
		if (totalDist != 0)
			return totalDist;
		for (int i = 0; i < order.length; i++)
			totalDist += table.getDistance(getTownAtPosition(i), getTownAtPosition(i + 1));
		return totalDist;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		int total = 0;
		for (int i = 0; i < order.length; i++) {
			sb.append("[").append(i).append("]");
			sb.append(table.getTown(getTownAtPosition(i))).append(" ==");

			int dist = table.getDistance(getTownAtPosition(i), getTownAtPosition(i + 1));
			total += dist;

			sb.append(dist);
			sb.append("==> ").append("\n");
		}
		sb.append("[0]").append(table.getTown(getTownAtPosition(0)));
		sb.append("\n").append("  Total: ").append(total);
		return sb.toString();
	}
}