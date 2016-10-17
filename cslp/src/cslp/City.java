package cslp;

public class City {
	// Number of service areas (max 65,535)
	public static int noAreas; // uint8_t
	
	public City(int noAreas) {
		City.noAreas = noAreas;
	}
}
