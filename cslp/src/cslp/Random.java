package cslp;

public class Random {
	// this class computes the erlangk distributino
	public static int erlangk(float disposalDistrRate, short disposalDistrShape) {
		double erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			double rand = Math.random();
			double rate = -1/disposalDistrRate;
			double logged = Math.log(rand); // log e instead becuase ln in lecture slides!!!!
			double result = rate*logged;
			erlangk += result;
		}
		double convertToSec = erlangk*60*60;
		int finalresult = (int) Math.round(convertToSec);
		return finalresult;
	}
}
