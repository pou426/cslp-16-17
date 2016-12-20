package cslp;

import java.util.ArrayList;



// RUN this algorithm 50 times to find the minimum of minduration???
// this is good for super big ...

/**
 * implement an algorithm such that it randomly compute a path. 
 * find upper and lower bound and pick the route that lies within.
 * ... 
 * how to find upper and lower bound:
 * http://myarnell.diymaths.com/Decision/D2/TSP.html
 * 
 * @author home
 *
 */
public class RandomPath {

	private int minDuration;
	private int[] shortestPath;
	
	public int getMinDuration() {
		return this.minDuration;
	}
	/**
	 * Creates random route 
	 * @param routeLayout
	 * @return int[]		matrix only contains bins, does not contain depots at the beginning and end
	 */
	public int[] getRoute(int[][] routeLayout) {
		NearestNeighbour nn = new NearestNeighbour();
		int[] nnRoute = nn.getRoute(routeLayout);
		
		int upperbound = nn.getMinDuration();
		System.out.println("upper bound = "+upperbound);
		
		int len = routeLayout.length - 1;
		ArrayList<Integer> indices = new ArrayList<Integer>();

		int idx = 0;
		int randomIdx;
		int prevLocation = 0;
		int nextLocation;
		int randomDuration = 0;
		int[] randomRoute = new int[len]; // does not contain depot at the beginning or end
		boolean isDone = false;
		while (!isDone) {
			idx=0;
			randomDuration = 0;
			indices.clear();
			for (int i = 0; i < len; i++) {
				indices.add(i+1);
			}
			System.out.println("arrayList: "+indices.toString());
			while (!(indices.isEmpty())) {
				randomIdx = (int) Math.floor(Math.random()*(indices.size()));
				nextLocation = indices.get(randomIdx);
				randomRoute[idx] = nextLocation;
				idx++;
				indices.remove(randomIdx);
				randomDuration += routeLayout[prevLocation][nextLocation];
				prevLocation = nextLocation;
			}			
			if (randomDuration <= upperbound) {
				isDone = true;
			}
		}
		
		this.minDuration = randomDuration;
		this.shortestPath = randomRoute;
		
		updateShortestPath();
		
		return this.shortestPath;
	}
	
	// updates attribute to include depot at beginning and end
	public void updateShortestPath() {
		int len = this.shortestPath.length;
		int copyLen = len + 2;
		int[] copy = new int[copyLen];
		copy[0] = 0;
		copy[copyLen-1] = 0;
		for (int i = 1; i < copyLen-1; i++) {
			copy[i] = this.shortestPath[i-1];
		}
		this.shortestPath = copy;
	}

	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
		getRoute(routeLayout); // this updates the attributes
		
		ArrayList<Integer> serviceQueue = new ArrayList<>();
		String serviceQueueString = "serviceQueue: ";
		for (int i = 1; i < this.shortestPath.length-1; i++) {
			int subGraphLocation = this.shortestPath[i];
			int location = requiredVertices[subGraphLocation];
			serviceQueue.add(location);
			serviceQueueString += Integer.toString(location)+" -> ";
		}
		serviceQueueString += "END";
		return serviceQueue;
		
	}

}
