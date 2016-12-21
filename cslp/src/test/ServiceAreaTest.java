package test;

import static org.junit.Assert.*;

import org.junit.Test;

import cslp.ServiceArea;

public class ServiceAreaTest {

	short areaIdx = 0;
	float serviceFreq = (float) 0.5;
	float thresholdVal = (float) 0.5;
	int noBins = 2;
	int[][] roadsLayout = {{0,1,2},
	                       {3,0,4},
	                       {7,2,0}};
	ServiceArea serviceArea = new ServiceArea(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
	
	@Test
	public void testServiceArea1() {
		int result = serviceArea.getTimeBetweenLocations(1, 2); 
		assertEquals(4, result);
		
		int result1 = serviceArea.getTimeBetweenLocations(2, 1); 
		assertEquals(2, result1);
		
		int result2 = serviceArea.getTimeBetweenLocations(0, 2);
		assertEquals(2, result2);
//		int result1 = serviceArea.getTimeBetweenLocations(2, 1);
//		assertEquals(7, result1);
	}
	
	@Test
	public void testServiceArea2() {
		int result = serviceArea.getTimeBetweenLocations(2, 1);
		assertEquals(2, result);
	}
	
	int[][] minDistMatrix = {{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
							 { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}};

	int[] requiredVertices = {0, 1, 3, 5, 7}; // all requiring vertix
	
	@Test
	public void testRouteLayout() {
		int[][] result = serviceArea.createRouteLayout(minDistMatrix, requiredVertices);
		int[][] actuals = {{ 0, 1, 3, 5, 7},
						   { 0, 1, 3, 5, 7}, 
						   { 0, 1, 3, 5, 7}, 
						   { 0, 1, 3, 5, 7}, 
						   { 0, 1, 3, 5, 7}};
		assertArrayEquals(result, actuals);
	}
	
	
	

}
