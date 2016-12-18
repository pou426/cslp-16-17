package test;

import static org.junit.Assert.*;

import org.junit.Test;

import cslp.Bag;

public class BagTest {

	@Test
	public void test() {
		float bagWeightMin = Bag.getBagWeightMin();
		float bagWeightMax = Bag.getBagWeightMax();
		Bag bag = new Bag();
		float weight;
//		float prevWeight;
		for (int i = 0; i < 100000; i++) {
			weight = bag.getWeight();
//			prevWeight = weight;
			assertTrue(weight <= bagWeightMax);
			assertTrue(weight >= bagWeightMin);
		}
	}

}
