package cslp;

import java.util.Deque;
import java.util.LinkedList;

/**
 * copied code and modified from Tushar Roy
 * source from github
 * 
 * References:
 * https://github.com/mission-peace/interview/blob/master/src/com/interview/graph/FloydWarshallAllPairShortestPath.java
 * @author home
 *
 */
public class FloydWarshall {

	private static final int INF = Integer.MAX_VALUE;
	
	public int[][] getMinDistMatrix(int[][] roadsLayout) {
		// distanceMatrix has (i,j) element INF if there is no path from i to j
		int[][] minDistMatrix = new int[roadsLayout.length][roadsLayout.length];
		int[][] path = new int[roadsLayout.length][roadsLayout.length];
		
		for (int i = 0; i < roadsLayout.length; i++) {
			for (int j = 0; j < roadsLayout[i].length; j++) {
				if (roadsLayout[i][j] == -1) {
					minDistMatrix[i][j] = INF;
				} else {
					minDistMatrix[i][j] = roadsLayout[i][j];
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
					if (minDistMatrix[i][k] == INF || minDistMatrix[k][j] == INF) {
						continue;
					}
					if (minDistMatrix[i][j] > minDistMatrix[i][k] + minDistMatrix[k][j]) {
						minDistMatrix[i][j] = minDistMatrix[i][k] + minDistMatrix[k][j];
						path[i][j] = path[k][j];
					}
				}
			}
		}
		
		for (int i = 1; i < path.length; i++) {
			if (path[i][0] == -1) {
				System.out.println("Error error no route back to depot. stupid");
				Error.throwError("Stupid!! no route back to depot");
			}
		}
		
		return minDistMatrix;
	}
	
	public void printPath(int[][] path, int start, int end) {
		if (start < 0 || end < 0 || start >= path.length || end >= path.length) {
			throw new IllegalArgumentException();
		}
		
		System.out.println("Actual path - between "+start+" "+end);
		Deque<Integer> stack = new LinkedList<>();
		stack.addFirst(end);
		while (true) {
			end = path[start][end];
			if (end == -1) {
				return;
			}
			stack.addFirst(end);
			if (end == start) {
				break;
			}
		}
		
		while (!stack.isEmpty()) {
			System.out.println(stack.pollFirst() + " ");
		}
		
		System.out.println();
	}
	
	public void printMinDistMatrix(int[][] minDistMatrix) {
		System.out.println("Minimum Distance Matrix");
		for (int i = 0; i < minDistMatrix.length; i++) {
			for (int j = 0; j < minDistMatrix.length; j++) {
				System.out.print(minDistMatrix[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public void printPath(int[][] path) {
    	System.out.println("Path Matrix");
    	for (int i = 0; i < path.length; i++) {
    		for (int j = 0; j < path.length; j++) {
    			System.out.print(path[i][j] + " ");
    		}
    		System.out.println("");
    	}
	}
	
}
