package ch.dritz.zhaw.ci.tsp;

import java.io.File;
import java.io.IOException;

/**
 * @author D. Ritz
 */
public class TravelingSalesman
{
	private Table tab;

	public TravelingSalesman(File file)
		throws IOException
	{
		tab = Parser.parse(file);
	}

	private void initialize()
	{

	}

	public void findSolution()
	{
		initialize();

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
		throws IOException
	{
		if (args.length != 1) {
			System.err.println("Usage: TravelingSalesman <input-file>");
			System.exit(1);
		}

		TravelingSalesman tsp = new TravelingSalesman(new File(args[0]));
		tsp.findSolution();
	}
}
