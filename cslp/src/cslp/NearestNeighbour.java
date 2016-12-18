package cslp;

import java.util.ArrayList;

/**
 * this method always work since u have precomputed the graph with floyd warshall algorithm
 * @author home
 *
 */

public class NearestNeighbour {

	public int[] getRoute(int[][] routeLayout) {
		
		// returns a route that include depots (begining and end)
		
		// do checking...
		int routeLen = routeLayout.length+1;
		int[] route = new int[routeLen];
		// starts and ends at 0
		int len = routeLayout.length;
		boolean[] visited = new boolean[len];
		for (boolean b : visited) {
			b = false;
		}
		
		visited[0] = true;
		
		boolean keepGoing = true;
		
		int currLocation = 0; // always start at depot
		int idx = 0;

		route[idx] = currLocation;
		idx++;
		
		int minDist = Integer.MAX_VALUE;
		int nn = -1;
		
		while (keepGoing) {
			for (int i = 0; i < len; i++) {
				if ((i!=currLocation) && (i!=0) && !visited[i]) {
					if (routeLayout[currLocation][i] < minDist) {
						minDist = routeLayout[currLocation][i];
						nn = i;
					}
				}
			}
			if (nn == -1) { 
				nn = 0;
				route[idx] = nn;
				keepGoing = false;
			} else {
				route[idx] = nn;
				currLocation = nn;
				visited[nn] = true;
			}
			minDist = Integer.MAX_VALUE;
			nn = -1;
			idx++;
		}
		System.out.print("Route is: ");
		for (int r : route) {
			System.out.print(r+" -> ");
		}
		System.out.println();
		return route; // index in the subgraph form
	}
	
	// input ServiceArea instance so directly modify the sa's serviceQueue
	// instead of creating another arraylist?
	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
		int[] route = getRoute(routeLayout);
		
		int len = route.length - 2; // remove depots at the start and end
		ArrayList<Integer> serviceQueue = new ArrayList<>();
		System.out.print("serviceQueue: ");
		for (int i = 1; i < route.length-1; i++) {
			int subGraphLocation = route[i];
			int location = requiredVertices[subGraphLocation];
			serviceQueue.add(location);
			System.out.print(location+" -> ");
		}
		System.out.println();
		return serviceQueue;
	}
	
	
	public static void main(String[] args) {
		int[][] routeLayout = { { 0, 1, 2, 3, 5},
								{ 1, 0, 2, 4, 7},
								{ 5, 2, 0, 3, 1},
								{ 1, 1, 4, 0, 2},
								{ 3, 1, 4, 1, 0}};
		NearestNeighbour nearestNeighbour = new NearestNeighbour();
		int[] route = nearestNeighbour.getRoute(routeLayout);
		System.out.println("route : ");
		for (int i : route) {
			System.out.print(i+" ");
		}
	}
}
