package test;

import static org.junit.Assert.*;

import org.junit.Test;

import cslp.NearestNeighbour;;

public class NearestNeighbourTest {

	NearestNeighbour nearestNeighbour = new NearestNeighbour();
	
	@Test
	public void testGetRoute() {
		int[][] routeLayout = {{ 0, 1, 2, 3},
							   { 1, 0, 2, 3},
							   { 1, 2, 0, 3},
							   { 1, 2, 3, 0}};
		
		int[] result = nearestNeighbour.getRoute(routeLayout);
		int[] actuals = {0, 1, 2, 3, 0};
		
		assertArrayEquals(result, actuals);
		
		int[][] routeLayout1 = {{ 0, 10,  5,  2},
								{15,  0, 20, 21},
								{12,  4,  0,  1},
								{ 1, 20, 44,  0}};
		
		int[] result1 = nearestNeighbour.getRoute(routeLayout1);
		int[] actuals1 = {0, 3, 1, 2, 0};
		
		assertArrayEquals(result1, actuals1);
		
	}
}
