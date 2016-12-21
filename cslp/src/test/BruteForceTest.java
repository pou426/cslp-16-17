package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

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

		int[] requiredVertices = {0, 4, 7, 9};
		ArrayList<Integer> result = bruteForce.getServiceQueue(routeLayout, requiredVertices);
//		int[] actuals = {0, 1, 2, 3, 0};
		int[] actuals = {4, 7, 9};

		assertArrayEquals(result, actuals);
	}
	
	@Test
	public void testGetRoute1() {
		int[][] routeLayout = {{ 0, 3, 4, 4},
				   			   { 1, 0, 9, 2},
				   			   { 9, 8, 0, 1},
				   			   { 1, 1,10, 0}};

		int[] requiredVertices = {0, 1, 2, 3};
		ArrayList<Integer> result = bruteForce.getServiceQueue(routeLayout, requiredVertices);
//		int[] actuals = {0, 1, 2, 3, 0};
		int[] actuals = {2, 3, 1};

		assertArrayEquals(result, actuals);
	}
	private void assertArrayEquals(ArrayList<Integer> result, int[] actuals) {
		// TODO Auto-generated method stub
		if (result.size() != actuals.length) {
			fail();
		}
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) != actuals[i]) {
				fail();
			}
		}
	}

}
