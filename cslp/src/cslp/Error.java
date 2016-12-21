package cslp;

public class Error {

	/**
	 * Prints error using stderr
	 *  
	 * @param message		an error message containing error information
	 */
	public static void throwError(String message) {
		System.err.println(message + "\tThe simulation will terminate.");
		System.exit(1);
	}
}
