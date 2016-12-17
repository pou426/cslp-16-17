package cslp;

import java.util.ArrayList;

/**
 * this method always work since u have precomputed the graph with floyd warshall algorithm
 * @author home
 *
 */
public class NearestNeighbour {

	public ArrayList<Integer> getRoute(int[][] routeLayout) {
		ArrayList<Integer> route = new ArrayList<Integer>();
		// starts and ends at 0
		int len = routeLayout.length;
		boolean[] visited = new boolean[len];
		for (boolean b : visited) {
			b = false;
		}
		visited[0] = true;
		
		boolean keepGoing = true;
		
		int currLocation = 0; // always start at depot
		route.add(currLocation);
		
		int minDist = Integer.MAX_VALUE;
		int nn = -1;
		
		while (keepGoing) {
//			System.out.println("currLocation : "+currLocation);
			for (int i = 0; i < len; i++) {
//				System.out.println("current i : "+i);
				if (i == currLocation) {
					// ignore 
//					System.out.println("(i == currLocation)");
				}
				else if (i == 0) {
					// ignore
//					System.out.println("(i == 0)");
				}
				else if (visited[i]) {
//					System.out.println(i+" has been visited.");
				}
				else if (!visited[i]) {
//					System.out.println("Min dist = "+minDist);
//					System.out.println("considering dist = "+routeLayout[currLocation][i]);
					if (routeLayout[currLocation][i] < minDist) {
						minDist = routeLayout[currLocation][i];
						nn = i;
					}
//					System.out.println("current nn : "+nn);
				}
			}
			if (nn == -1) { // go to nearest neighbour and keep searching
				// finish and go back to depot?
				nn = 0;
				route.add(nn);
				keepGoing = false;
			} else {
				route.add(nn);
				currLocation = nn;
				visited[nn] = true;
			}
			minDist = Integer.MAX_VALUE;
			nn = -1;
		}
		
		return route;
	}
	
	public static void main(String[] args) {
		int[][] routeLayout = { { 0, 1, 2, 3, 5},
								{ 1, 0, 2, 4, 7},
								{ 5, 2, 0, 3, 1},
								{ 1, 1, 4, 0, 2},
								{ 3, 1, 4, 1, 0}};
		NearestNeighbour nearestNeighbour = new NearestNeighbour();
		ArrayList<Integer> route = nearestNeighbour.getRoute(routeLayout);
		System.out.println("route is : ");
		for (int i : route) {
			System.out.print(i+" ");
		}
	}
}
