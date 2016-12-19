package cslp;

import java.util.logging.Logger;

public class FloydWarshall {

	public static final Logger LOGGER = Logger.getLogger(FloydWarshall.class.getName());

	private static final int INF = Integer.MAX_VALUE;

	/**
	 * Compute the shortest path between all paris using the Floyd Warshall algorithm
	 * @param roadsLayout	roadsLayout matrix from input file
	 * @return	int[][]		matrix with elements (i,j) for distance between i and j
	 */
	public int[][] getMinDistMatrix(int[][] roadsLayout) {
		// distanceMatrix has (i,j) element INF if there is no path from i to j
		int[][] minDist = new int[roadsLayout.length][roadsLayout.length];
		int[][] path = new int[roadsLayout.length][roadsLayout.length];
		
		for (int i = 0; i < roadsLayout.length; i++) {
			for (int j = 0; j < roadsLayout[i].length; j++) {
				if (roadsLayout[i][j] == -1) {
					minDist[i][j] = INF;
				} else {
					minDist[i][j] = roadsLayout[i][j];
				}
				if (roadsLayout[i][j] != -1 && i != j) {
					path[i][j] = i;
				} else {
					path[i][j] = -1;
				}
			}
		}
		
		for (int k = 0; k < roadsLayout.length; k++) {
			for (int i = 0; i < roadsLayout.length; i++) {
				for (int j = 0; j < roadsLayout.length; j++) {
					if (minDist[i][k] == INF || minDist[k][j] == INF) {
						continue;
					}
					if (minDist[i][j] > minDist[i][k] + minDist[k][j]) {
						minDist[i][j] = minDist[i][k] + minDist[k][j];
						path[i][j] = path[k][j];
					}
				}
			}
		}
		
		for (int i = 1; i < path.length; i++) {
			if (path[i][0] == -1) {
				LOGGER.warning("Warning: No route back to depot.");
			}
		}
		
		return minDist;
	}

	public String minDistToString(int[][] minDistMatrix) {
		String str = "Minimum Distance Matrix: \n";
		for (int i = 0; i < minDistMatrix.length; i++) {
			for (int j = 0; j < minDistMatrix.length; j++) {
				str += Integer.toString(minDistMatrix[i][j])+" ";
			}
			str += "\n";
		}
		return str;
	}

	public void printPath(int[][] path) {
    	System.out.println("Path Matrix");
    	for (int i = 0; i < path.length; i++) {
    		for (int j = 0; j < path.length; j++) {
    			System.out.print(path[i][j] + " ");
    		}
    		System.out.println();
    	}
	}
	
}
