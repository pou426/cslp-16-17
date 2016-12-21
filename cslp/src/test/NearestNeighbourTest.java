package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cslp.NearestNeighbour;;

public class NearestNeighbourTest {

	NearestNeighbour nearestNeighbour = new NearestNeighbour();
	@Test
	public void test() {
		int[][] routeLayout = {{ 0, 1, 2, 3},
							   { 3, 0, 5, 1},
							   { 1, 3, 9, 6},
							   { 9, 8, 7, 0}};
		int[] requiredVertices = {0, 1, 4, 6, 9};
		
		ArrayList<Integer> result = nearestNeighbour.getServiceQueue(routeLayout, requiredVertices);
		ArrayList<Integer> actuals = new ArrayList<Integer>();
		actuals.add(1);
		actuals.add(6);
		actuals.add(4);
		
		assertArrayEquals(actuals, result);
		
		int resultDurr = nearestNeighbour.getMinDuration();
		int actualDurr = 10;
		
		assertEquals(actualDurr, resultDurr);
	}
	
	@Test
	public void test1() {
		int[][] routeLayout = {{ 0, 20, 2, 3},
							   { 3, 0, 5, 15},
							   { 1, 13, 9, 6},
							   { 9, 8, 17, 0}};
		int[] requiredVertices = {0, 1, 4, 6, 9};
		
		ArrayList<Integer> result = nearestNeighbour.getServiceQueue(routeLayout, requiredVertices);
		ArrayList<Integer> actuals = new ArrayList<Integer>();
		actuals.add(4);
		actuals.add(6);
		actuals.add(1);
		
		assertArrayEquals(actuals, result);
		
		int resultDurr = nearestNeighbour.getMinDuration();
		int actualDurr = 19;
		
		assertEquals(actualDurr, resultDurr);
	}

	private void assertArrayEquals(ArrayList<Integer> result,
			ArrayList<Integer> actuals) {
		// TODO Auto-generated method stub
		if (result.size() != actuals.size()) {
			fail();
		}
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
			System.out.println(actuals.get(i));
			if (result.get(i) != actuals.get(i)) {
				fail();
			}
		}
	}
		
}
