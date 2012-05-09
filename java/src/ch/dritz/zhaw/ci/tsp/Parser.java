package ch.dritz.zhaw.ci.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author D. Ritz
 */
public class Parser
{
	public static Table parse(File input)
		throws IOException
	{
		List<String> towns = new ArrayList<String>();
		List<int[]> distances = new ArrayList<int[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input));
			String line;
			boolean part1 = true;
			while ((line = br.readLine()) != null) {
				if ("".equals(line)) {
					part1 = false;
					continue;
				}

				if (part1) {
					towns.add(line.trim());

				} else {
					String[] dist = line.split("\\s+");

					int i = distances.size();
					int distArray[] = new int[i+1];
					for (int j = 0; j <= i; j++) {
						if (j >= dist.length)
							continue;
						if ("-".equals(dist[j]))
							continue;
						distArray[j] = Integer.valueOf(dist[j]);
					}
					distArray[i] = -1;

					distances.add(distArray);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}

		Table tab = new Table(
			towns.toArray(new String[towns.size()]),
			distances.toArray(new int[distances.size()][]));
		return tab;
	}
}
