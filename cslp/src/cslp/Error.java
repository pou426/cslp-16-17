package cslp;

/**
 * Error class
 *
 */
public class Error {

	/**
	 * prints error and set containsError flag to true 
	 * @param message		an error message containing error information
	 */
	public static void throwError(String message) {
		System.err.println(message);
		System.err.println("The simulation will terminate.");
		System.exit(1);
	}
}
