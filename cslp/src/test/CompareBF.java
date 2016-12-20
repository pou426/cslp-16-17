package test;
import java.util.Arrays;

import cslp.BruteForce;

/**
 * This file creates testing for brute-force to identify the size of matrix 
 * which gives least complexity
 * 
 */
public class CompareBF {

	public void testing(int n) {
		BruteForce bf = new BruteForce();
		int[][] matrix = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = (int) Math.floor(Math.random()*10);
				System.out.println(matrix[i][j]);
			}
		}
		int[] route = bf.getRoute(matrix);
		System.out.println(Arrays.toString(route));
		System.out.println();
	}
	public static void main(String[] args) {
		CompareBF cbf = new CompareBF();
		cbf.testing(10); // acceptable performance
		
//		cbf.testing(13); // gets too slow from 13 onwards
		
	}
}
