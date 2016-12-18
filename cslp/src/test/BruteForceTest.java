package test;

import static org.junit.Assert.*;

import org.junit.Test;

import cslp.BruteForce;

public class BruteForceTest {

	BruteForce bruteForce = new BruteForce();
	@Test
	public void testGetRoute() {
		int[][] routeLayout = {{ 0, 1, 2, 3},
				   			   { 1, 0, 2, 3},
				   			   { 1, 2, 0, 3},
				   			   { 1, 2, 3, 0}};

		int[] result = bruteForce.getRoute(routeLayout);
		int[] actuals = {0, 1, 2, 3, 0};

		assertArrayEquals(result, actuals);

	}

}
